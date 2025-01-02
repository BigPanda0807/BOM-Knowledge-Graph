package com.panda.knowledgegraph.utils;

import com.panda.knowledgegraph.dto.BomMap;
import com.panda.knowledgegraph.dto.InputBomMap;
import com.panda.knowledgegraph.dto.InputBomNode;
import com.panda.knowledgegraph.dto.InputConsistOf;
import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.entity.relationship.ConsistOf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class RecommendUtils {

    @Autowired
    Neo4jUtils neo4jUtils;

    public void createKnowledgeGraph() {
        // 获取图谱中节点和关系
        BomMap bomKnowledgeGraph = neo4jUtils.getBomKnowledgeGraph();
        // 图谱文件
        String directoryPath = "D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB";
        String fileName = "KnowledgeGraph.data";
        try {
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            // 写入数据
            bufferedWriter.write("t|id|name|type|properties");
            bufferedWriter.newLine();
            for (BomNode bomNode : bomKnowledgeGraph.getBomNodeList()) {
                bufferedWriter.write("v|" + bomNode.getId() + "|" + bomNode.getName() + "|" + bomNode.getType() + "|" + bomNode.getProperties());
                bufferedWriter.newLine();
            }
            for (ConsistOf consistOf : bomKnowledgeGraph.getRelationshipList()) {
                Long source = consistOf.getSourceId();
                Long target = consistOf.getTargetId();
                bufferedWriter.write("e|" + source + "|" + target + "|" + consistOf.getType() + "|" + consistOf.getQuantity() + "|" + consistOf.getProperties());
                bufferedWriter.newLine();
            }
            bufferedWriter.write("t|#|-1");
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createQueryGraph(String recommendName) {
        InputBomMap inputBomMap = neo4jUtils.getInputBomMap(recommendName);
        String queryDirectoryPath = "D:\\GitStorage\\knowledge-graph\\src\\main\\resources\\static\\graphDB";
        String queryFileName = recommendName + ".input";
        try {
            File directory = new File(queryDirectoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, queryFileName);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            // 写入数据
            bufferedWriter.write("t|id|name|type|properties");
            bufferedWriter.newLine();
            for (InputBomNode inputBomNode : inputBomMap.getBomNodeList()) {
                //v|id|name|type|properties
                bufferedWriter.write("v|" + inputBomNode.getInputId() + "|" + inputBomNode.getName() + "|" + inputBomNode.getType() + "|" + inputBomNode.getProperties());
                bufferedWriter.newLine();
            }
            for (InputConsistOf inputConsistOf : inputBomMap.getRelationshipList()) {
                Long source = inputConsistOf.getSourceId();
                Long target = inputConsistOf.getTargetId();
                // e|source|target|type|quantity|properties
                bufferedWriter.write("e|" + source + "|" + target + "|" + inputConsistOf.getType() + "|" + inputConsistOf.getQuantity() + "|" + inputConsistOf.getProperties());
                bufferedWriter.newLine();
            }
            bufferedWriter.write("t|#|-1");
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
