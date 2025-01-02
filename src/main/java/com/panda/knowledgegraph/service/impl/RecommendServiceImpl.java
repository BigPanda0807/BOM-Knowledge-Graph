package com.panda.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.contants.NodeEntityTypes;
import com.panda.knowledgegraph.dao.BomNodeRepository;
import com.panda.knowledgegraph.dto.InputBomMap;
import com.panda.knowledgegraph.dto.Mapping;
import com.panda.knowledgegraph.dto.ResultTreeNode;
import com.panda.knowledgegraph.dto.TreeNode;
import com.panda.knowledgegraph.entity.Recommend;
import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.mapper.RecommendMapper;
import com.panda.knowledgegraph.service.BomService;
import com.panda.knowledgegraph.service.RecommendService;
import com.panda.knowledgegraph.utils.Neo4jUtils;
import com.panda.knowledgegraph.utils.RecommendUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecommendServiceImpl extends ServiceImpl<RecommendMapper, Recommend> implements RecommendService {

    @Autowired
    Neo4jUtils neo4jUtils;

    @Autowired
    RecommendUtils recommendUtils;

    @Autowired
    RecommendMapper recommendMapper;

    @Autowired
    BomNodeRepository bomNodeRepository;

    @Autowired
    BomService bomService;

    @Override
    public void importUserInputToNeo4j(Long id) throws JsonProcessingException {
        Recommend recommend = this.getById(id);
        // json字符串转换
        ObjectMapper objectMapper = new ObjectMapper();
        TreeNode[] treeNodes = objectMapper.readValue(recommend.getUserInputStructure(), TreeNode[].class);
        List<TreeNode> treeNodeList = new ArrayList<>();
        treeNodeList.add(treeNodes[0]);
        // 先删除
        neo4jUtils.deleteInputByName(recommend.getRecommendName());
        // 递归遍历
        recursiveCreateNode(treeNodeList, recommend.getRecommendName());
        recursiveCreateRelationship(treeNodeList, recommend.getRecommendName());
    }

    @Override
    public InputBomMap getInput(Long id) {
        return neo4jUtils.getInputBomMap(this.getById(id).getRecommendName());
    }

    @Override
    public List<Mapping> getStructureRecommend(Long id) {
        String recommendName = this.getById(id).getRecommendName();
        recommendUtils.createKnowledgeGraph();
        recommendUtils.createQueryGraph(recommendName);
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:5000/recommend/getMapping?recommendName=" + recommendName;
        ResponseEntity<List<Map<Long, Long>>> entity = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<Long, Long>>>() {
        });
        List<Mapping> mappingList = new ArrayList<>();
        if (entity.getBody().size() == 0) {
            return mappingList;
        }
        for (Map<Long, Long> map : entity.getBody()) {
            List<Long> ids = new ArrayList<>(map.keySet());
            List<Map<String, Object>> relationships = bomNodeRepository.queryBomPartRelationship(ids);
            ArrayList<BomNode> bomNodes = new ArrayList<>();
            for (Long item : ids) {
                bomNodes.add(bomNodeRepository.queryBomNodeById(item));
            }
            Mapping mapping = new Mapping();
            for (Long key : map.keySet()) {
                if (map.get(key) == 1) {
                    mapping.setParentId(key);
                    break;
                }
            }
            mapping.setBomNodeList(bomNodes);
            mapping.setRelationshipList(relationships);
            mappingList.add(mapping);
        }
        System.out.println();
        return mappingList;
    }

    @Override
    public R<String> getRecommendFinishedProduct(String code) throws JsonProcessingException {
        List<BomNode> bomNodeList = bomNodeRepository.queryFinishedLine(code);
        BomNode finishedBom = null;
        for (BomNode node : bomNodeList) {
            if (node.getType().equals("成品（产品）")) {
                finishedBom = node;
                break;
            }
        }
        return bomService.getFinishedProductNameByCode(finishedBom.getCode());
    }

    @Override
    public void recommendResultReturnToKG(Long id) throws JsonProcessingException {
        Recommend recommend = this.getById(id);
        ObjectMapper objectMapper = new ObjectMapper();
        ResultTreeNode[] resultTreeNodes = objectMapper.readValue(recommend.getRecommendResult(), ResultTreeNode[].class);
        List<ResultTreeNode> resultTreeNodeList = new ArrayList<>();
        resultTreeNodeList.add(resultTreeNodes[0]);
        neo4jUtils.deleteResultByLabel("result" + id);
        // code问题之后再考虑
        recursiveCreateResultNode(resultTreeNodeList, "result" + id);
        recursiveCreateResultRelationship(resultTreeNodeList, "result" + id);
    }

    @Override
    public boolean addRecommend(String recommendName) {
        if (recommendMapper.getByRecommendName(recommendName) == null) {
            Recommend recommend = new Recommend();
            recommend.setRecommendName(recommendName);
            recommend.setUserInputStructure("[{\"id\":1,\"parentId\":0,\"name\":\"成品\",\"type\":\"成品（产品）\",\"properties\":\"{}\",\"process\":\"\",\"processProperties\":\"\",\"count\":\"\",\"children\":[{\"id\":2,\"parentId\":1,\"name\":\"半成品\",\"type\":\"半成品（中间产品）\",\"properties\":\"{}\",\"process\":\"组装\",\"processProperties\":\"{}\",\"count\":1,\"children\":[{\"id\":3,\"parentId\":2,\"name\":\"零部件\",\"type\":\"零部件（原材料）\",\"properties\":\"{}\",\"process\":\"组装\",\"processProperties\":\"{}\",\"count\":1,\"children\":[]}]}]}]");
            recommend.setRecommendResult("[]");
            recommend.setUserInputNodeCount(3);
            recommend.setStartId(4);
            return this.save(recommend);
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteRecommend(Long id) {
        Recommend recommend = this.getById(id);
        neo4jUtils.deleteInputByName(recommend.getRecommendName());
        return this.removeById(id);
    }

    public void recursiveCreateNode(List<TreeNode> bomList, String label) {
        if (bomList.size() == 0) {
            return;
        }
        for (TreeNode treeNode : bomList) {
            if (treeNode.getType().equals("成品（产品）")) {
                neo4jUtils.createUserInputNode(treeNode.getId(), label, treeNode.getName(), treeNode.getType(), treeNode.getProperties(), NodeEntityTypes.FINISHEDPRODUCT.getType());
            } else if (treeNode.getType().equals("半成品（中间产品）")) {
                neo4jUtils.createUserInputNode(treeNode.getId(), label, treeNode.getName(), treeNode.getType(), treeNode.getProperties(), NodeEntityTypes.SEMIFINISHEDPRODUCT.getType());
            } else if (treeNode.getType().equals("零部件（原材料）")) {
                neo4jUtils.createUserInputNode(treeNode.getId(), label, treeNode.getName(), treeNode.getType(), treeNode.getProperties(), NodeEntityTypes.COMPONENT.getType());
            }
            recursiveCreateNode(treeNode.getChildren(), label);
        }
    }

    public void recursiveCreateRelationship(List<TreeNode> bomList, String label) {
        if (bomList.size() == 0) {
            return;
        }
        for (TreeNode treeNode : bomList) {
            if (treeNode.getChildren().size() == 0) {
                continue;
            }
            for (TreeNode childTreeNode : treeNode.getChildren()) {
                neo4jUtils.createUserInputRelationship(label, childTreeNode.getProcess(), childTreeNode.getCount(), childTreeNode.getId(), treeNode.getId(), childTreeNode.getProcessProperties());
            }
            recursiveCreateRelationship(treeNode.getChildren(), label);
        }
    }

    public void recursiveCreateResultNode(List<ResultTreeNode> bomList, String label) {
        if (bomList.size() == 0) {
            return;
        }
        for (ResultTreeNode treeNode : bomList) {
            if (treeNode.getType().equals("成品（产品）")) {
                neo4jUtils.createResultNode(treeNode.getId(), label, treeNode.getCode(), treeNode.getName(), treeNode.getType(), treeNode.getProperties(), NodeEntityTypes.FINISHEDPRODUCT.getType());
            } else if (treeNode.getType().equals("半成品（中间产品）")) {
                neo4jUtils.createResultNode(treeNode.getId(), label, treeNode.getCode(), treeNode.getName(), treeNode.getType(), treeNode.getProperties(), NodeEntityTypes.SEMIFINISHEDPRODUCT.getType());
            } else if (treeNode.getType().equals("零部件（原材料）")) {
                neo4jUtils.createResultNode(treeNode.getId(), label, treeNode.getCode(), treeNode.getName(), treeNode.getType(), treeNode.getProperties(), NodeEntityTypes.COMPONENT.getType());
            }
            recursiveCreateResultNode(treeNode.getChildren(), label);
        }
    }

    public void recursiveCreateResultRelationship(List<ResultTreeNode> bomList, String label) {
        if (bomList.size() == 0) {
            return;
        }
        for (ResultTreeNode treeNode : bomList) {
            if (treeNode.getChildren().size() == 0) {
                continue;
            }
            for (ResultTreeNode childTreeNode : treeNode.getChildren()) {
                neo4jUtils.createResultRelationship(label, childTreeNode.getProcess(), childTreeNode.getCount(), childTreeNode.getId(), treeNode.getId(), childTreeNode.getProcessProperties());
            }
            recursiveCreateResultRelationship(treeNode.getChildren(), label);
        }
    }
}
