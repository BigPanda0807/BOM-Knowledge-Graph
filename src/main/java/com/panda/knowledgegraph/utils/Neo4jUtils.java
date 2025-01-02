package com.panda.knowledgegraph.utils;

import com.panda.knowledgegraph.dao.BomNodeRepository;
import com.panda.knowledgegraph.dto.BomMap;
import com.panda.knowledgegraph.dto.InputBomMap;
import com.panda.knowledgegraph.dto.InputBomNode;
import com.panda.knowledgegraph.dto.InputConsistOf;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.summary.ResultSummary;
import org.neo4j.driver.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class Neo4jUtils {

    @Autowired
    Neo4jClient neo4jClient;

    @Autowired
    BomNodeRepository bomNodeRepository;

    // 创建用户输入的结构里的节点
    public void createUserInputNode(Integer inputId, String label, String name, String type, String properties, String anotherLabel) {
        neo4jClient.query("merge (b:" + label + " {inputId:$inputId,name:$name,type:$type,properties:$properties}) set b:inputBom:" + anotherLabel).bind(inputId).to("inputId").bind(name).to("name").bind(type).to("type").bind(properties).to("properties").run();
    }

    // 创建推荐结果里的节点
    public void createResultNode(Long resultId, String label, String code, String name, String type, String properties, String anotherLabel) {
        neo4jClient.query("merge (b:" + label + " {resultId:$resultId,code:$code,name:$name,type:$type,properties:$properties}) set b:resultBom:" + anotherLabel).bind(resultId).to("resultId").bind(code).to("code").bind(name).to("name").bind(type).to("type").bind(properties).to("properties").run();
    }

    // 创建用户输入的结构里的关系
    public void createUserInputRelationship(String label, String type, Integer quantity, Integer sourceId, Integer targetId, String properties) {
        neo4jClient.query("match (a:" + label + "{inputId:$targetId}),(b:" + label + "{inputId:$sourceId}) merge (a)<-[r:input_consist_of{type:$type,quantity:$quantity,sourceId:$sourceId,targetId:$targetId,properties:$properties}]-(b)").bind(targetId).to("targetId").bind(sourceId).to("sourceId").bind(type).to("type").bind(quantity).to("quantity").bind(properties).to("properties").run();
    }

    // 创建推荐结果里的关系
    public void createResultRelationship(String label, String type, Integer quantity, Long sourceId, Long targetId, String properties) {
        neo4jClient.query("match (a:" + label + "{resultId:$targetId}),(b:" + label + "{resultId:$sourceId}) merge (a)<-[r:result_consist_of{type:$type,quantity:$quantity,sourceId:$sourceId,targetId:$targetId,properties:$properties}]-(b)").bind(targetId).to("targetId").bind(sourceId).to("sourceId").bind(type).to("type").bind(quantity).to("quantity").bind(properties).to("properties").run();
    }

    // 删除用户输入的结构
    public void deleteInputByName(String recommendName) {
        neo4jClient.query("match (n:" + recommendName + ") detach delete n").run();
    }

    // 删除图谱内用户推荐结果
    public void deleteResultByLabel(String label) {
        neo4jClient.query("match (n:" + label + ") detach delete n").run();
    }

    // 查找用户输入的结构
    public InputBomMap getInputBomMap(String recommendName) {
        String queryNodeCypher = "MATCH (n:" + recommendName + ") RETURN n";
        List<InputBomNode> nodes = (List<InputBomNode>) neo4jClient.query(queryNodeCypher).fetchAs(InputBomNode.class).mappedBy((typeSystem, record) -> {
            return new InputBomNode(record.get("n").asNode());
        }).all();
        String queryRelationshipCypher = "match (a:" + recommendName + ")<-[r:input_consist_of]-(b:" + recommendName + ") return r";
        Collection<InputConsistOf> all = neo4jClient.query(queryRelationshipCypher).fetchAs(InputConsistOf.class).mappedBy((typeSystem, record) -> {
            Relationship relationship = record.get("r").asRelationship();
            long sourceId = relationship.get("sourceId").asLong();
            long targetId = relationship.get("targetId").asLong();
            long id = relationship.id();
            int quantity = relationship.get("quantity").asInt();
            String type = relationship.get("type").asString();
            String properties = relationship.get("properties").asString();
            return new InputConsistOf(id, type, quantity, sourceId, targetId, properties);
        }).all();
        List<InputConsistOf> relationships = new ArrayList<>(all);
        return new InputBomMap(nodes, relationships);
    }

    // 获取图谱中所有的节点和边
    public BomMap getBomKnowledgeGraph() {
        BomMap bomMap = new BomMap();
        bomMap.setBomNodeList(bomNodeRepository.findAllBomNode());
        bomMap.setRelationshipList(bomNodeRepository.findAllBomRelationship());
        return bomMap;
    }

    // 下面四个方法废弃 所有属性均可查到

    // 根据id查找节点的所有属性
    public Map findBomNodeProperties(Long bomNodeId) {
        return neo4jClient.query("MATCH (b:bom) WHERE id(b) = $bomNodeId RETURN b").bind(bomNodeId).to("bomNodeId").fetchAs(Map.class).one().get();
    }

    // 根据id查找关系的所有属性
    public Map findBomRelationshipProperties(Long relationshipId) {
        return neo4jClient.query("MATCH (a:bom)<-[r:consist_of]-(b:bom) WHERE id(r) = $relationshipId RETURN r").bind(relationshipId).to("relationshipId").fetchAs(Map.class).one().get();
    }

    // 为节点添加属性
    public boolean addBomNodeProperty(Long bomNodeId, String propertyName, String propertyValue) {
        ResultSummary result = neo4jClient.query("MATCH (b:bom) WHERE id(b) = $bomNodeId SET b += {`" + propertyName + "` : $propertyValue}")
                .bind(bomNodeId).to("bomNodeId")
                .bind(propertyValue).to("propertyValue")
                .run();
        if (result.counters().propertiesSet() >= 1) {
            return true;
        }
        return false;
    }

    // 为关系添加属性
    public boolean addBomRelationshipProperty(Long bomRelationshipId, String propertyName, String propertyValue) {
        ResultSummary result = neo4jClient.query("MATCH (a:bom)-[r:consist_of]-(b:bom) WHERE id(r) = $bomRelationshipId SET r += {`" + propertyName + "` : $propertyValue}")
                .bind(bomRelationshipId).to("bomRelationshipId")
                .bind(propertyValue).to("propertyValue")
                .run();
        if (result.counters().propertiesSet() >= 1) {
            return true;
        }
        return false;
    }
}
