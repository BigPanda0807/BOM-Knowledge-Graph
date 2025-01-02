package com.panda.knowledgegraph.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;

@Data
public class ManufacturingTechnique {

    private Long id;

    private String code;

    private String name;

    private Integer status;

    private String description;

    private String procedures;

    private String parameter;

    private String properties;

    private String sourceBomCode;

    private String targetBomCode;

    private String relatedEquipment;
}
