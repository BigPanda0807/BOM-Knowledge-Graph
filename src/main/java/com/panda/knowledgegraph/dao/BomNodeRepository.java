package com.panda.knowledgegraph.dao;

import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.entity.relationship.ConsistOf;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface BomNodeRepository extends Neo4jRepository<BomNode, Long> {
    // 查找所有节点 包含id和必填属性
    @Query("match (b:bom) return b")
    List<BomNode> findAllBomNode();

    // 查找所有关系 包含id和必填属性
    @Query("match (a:bom)<-[r:consist_of]-(b:bom) return r.quantity as quantity, r.type as type,  r.sourceCode as sourceCode, r.sourceId as sourceId, r.targetId as targetId, r.targetCode as targetCode, id(r) as rid, r.properties as properties")
    List<ConsistOf> findAllBomRelationship();

    // 创建Bom关系 targetCode <- sourceCode a <- b
    @Query("match (a:bom{code:$parentCode}),(b:bom{code:$code}) merge (a)<-[r:consist_of{type:$relationship,quantity:$quantity,sourceCode:$code,sourceId:id(b),targetCode:$parentCode,targetId:id(a),properties:$properties}]-(b)")
    void mergeRelationship(@Param("parentCode") String parentCode, @Param("code") String code, @Param("relationship") String relationship, @Param("quantity") Integer quantity, @Param("properties") String properties);

    // 创建设备和供应商的关系
    @Query("match (e:equipment),(s:supplier) where e.code=$equipmentCode AND s.code=$supplierCode merge (e)-[:supply]-(s)")
    void mergeEquipmentAndSupplierRelationship(@Param("equipmentCode") String equipmentCode, @Param("supplierCode") String supplierCode);

    // 创建Bom和供应商之间的关系
    @Query("match (b:bom),(s:supplier) where b.code=$bomCode AND s.code=$supplierCode merge (b)-[:supply]-(s)")
    void mergeBomAndSupplierRelationship(@Param("bomCode") String bomCode, @Param("supplierCode") String supplierCode);

    // 创建Bom和工艺之间的关系
    @Query("match (b1:bom),(b2:bom),(m:manufacturingTechnique) where b1.code=$sourceBomCode AND b2.code=$targetBomCode And m.code=$manufacturingTechniqueCode merge (b1)-[:apply]->(m)-[:apply]->(b2)")
    void mergeBomAndManufacturingTechniqueRelationship(@Param("sourceBomCode") String sourceBomCode, @Param("targetBomCode") String targetBomCode, @Param("manufacturingTechniqueCode") String manufacturingTechniqueCode);

    // 创建设备和工艺之间的关系
    @Query("match (e:equipment),(m:manufacturingTechnique) where e.code=$equipmentCode AND m.code=$manufacturingTechniqueCode merge (e)-[:use]-(m)")
    void mergeEquipmentAndManufacturingTechniqueRelationship(@Param("equipmentCode") String equipmentCode, @Param("manufacturingTechniqueCode") String manufacturingTechniqueCode);

    // 递归查询某Bom的组成
    @Query("match (start:bom)<-[r:consist_of *0..]-(other:bom) where start.code = $code return other")
    List<BomNode> queryBomPart(@Param("code") String code);

    // 查询推荐结果中 用户输入结构 结构所在的成品
    @Query("match (start:bom)-[r:consist_of *0..]->(other:bom) where start.code = $code return other")
    List<BomNode> queryFinishedLine(@Param("code") String code);

    // 查询节点之间的关系
    @Query("MATCH (n1)<-[r:consist_of]-(n2) WHERE ID(n1) IN $nodeIdsList AND ID(n2) IN $nodeIdsList RETURN r")
    List<Map<String, Object>> queryBomPartRelationship(@Param("nodeIdsList") List<Long> nodeIdsList);

    // 根据id查询节点
    @Query("match (n:bom) where id(n) = $id return n")
    BomNode queryBomNodeById(@Param("id") Long id);

    // 删除设备和工艺之间的关系
    @Query("MATCH (e:equipment{code:$equipmentCode})-[r:use]-(p:manufacturingTechnique{code:$processCode}) DELETE r")
    void deleteEAndMRelationship(@Param("equipmentCode") String equipmentCode, @Param("processCode") String processCode);

    // 删除工艺和bom节点之间的关系
    @Query("match (b1:bom)-[r1:apply]-(m:manufacturingTechnique)-[r2:apply]-(b2:bom) where b1.code=$sourceBomCode AND b2.code=$targetBomCode And m.code=$manufacturingTechniqueCode delete r1, r2")
    void deleteMAndBRelationship(@Param("sourceBomCode") String sourceBomCode, @Param("targetBomCode") String targetBomCode, @Param("manufacturingTechniqueCode") String manufacturingTechniqueCode);
}
