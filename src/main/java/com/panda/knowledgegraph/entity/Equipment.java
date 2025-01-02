package com.panda.knowledgegraph.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

@Data
public class Equipment {
    private Long id;

    // 设备代码
    private String code;

    // 设备名称
    private String name;

    // 设备类型:焊机 压机等
    private String type;

    // 设备状态
    private Integer status;

    // 设备规格
    private String technicalSpecifications;

    // 扩展属性 json
    private String properties;

    private String relatedProcess;
}
