package com.panda.knowledgegraph.entity.node;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node(labels = "manufacturingTechnique")
public class ManufacturingTechnique {
    @Id
    @GeneratedValue
    private Long id;

    // 工艺代码
    @Property("code")
    private String code;

    // 工艺名称
    @Property("name")
    private String name;

    // 工艺状态
    @Property("status")
    private Integer status;

    // 工艺描述
    @Property("description")
    private String description;

    // 工艺步骤
    @Property("procedures")
    private String procedures;

    // 工艺参数
    @Property("parameter")
    private String parameter;

    // 扩展属性 json
    @Property("properties")
    private String properties;
}
