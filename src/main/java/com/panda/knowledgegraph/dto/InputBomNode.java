package com.panda.knowledgegraph.dto;

import lombok.Data;
import org.neo4j.driver.types.Node;

@Data
public class InputBomNode {
    private Long inputId;
    private String name;
    private String type;
    private String properties;

    public InputBomNode(Node node) {
        this.inputId = node.get("inputId").asLong();
        this.name = node.get("name").asString(); // 假设节点中有一个名为"name"的属性
        this.type = node.get("type").asString();
        this.properties = node.get("properties").asString();
    }
}
