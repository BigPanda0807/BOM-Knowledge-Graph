package com.panda.knowledgegraph.entity.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = "bom")
public class BomNode {
    @Id
    @GeneratedValue
    private Long id;

    @Property("code")
    private String code;

    @Property("name")
    private String name;

    @Property("type")
    private String type;

    // 扩展属性 json
    @Property("properties")
    private String properties;
}
