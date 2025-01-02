package com.panda.knowledgegraph.entity.relationship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsistOf {

    private Long rid;

    private Long quantity;

    private String type;

    private String sourceCode;

    private Long sourceId;

    private String targetCode;

    private Long targetId;

    // 所有属性 包括扩展属性
    private String properties;
}
