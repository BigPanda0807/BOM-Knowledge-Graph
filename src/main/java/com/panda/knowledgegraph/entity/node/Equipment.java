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
@Node(labels = "equipment")
public class Equipment {
    @Id
    @GeneratedValue
    private Long id;

    // 设备代码
    @Property("code")
    private String code;

    // 设备名称
    @Property("name")
    private String name;

    // 设备类型:焊机 压机等
    @Property("type")
    private String type;

    // 设备状态
    @Property("status")
    private Integer status;

    // 设备规格
    @Property("technicalSpecifications")
    private String technicalSpecifications;

    // 扩展属性 json
    @Property("properties")
    private String properties;

}
