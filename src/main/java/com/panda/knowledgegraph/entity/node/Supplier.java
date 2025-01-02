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
@Node(labels = "supplier")
public class Supplier {
    @Id
    @GeneratedValue
    private Long id;

    // 供应商代码
    @Property("code")
    private String code;

    // 供应商名称
    @Property("name")
    private String name;

    // 供应商类型:制造商 批发商 零售商
    @Property("type")
    private String type;

    // 经营状态
    @Property("status")
    private Integer status;

    // 地址
    @Property("address")
    private String address;

    // 经营范围
    @Property("scope")
    private String scope;

    // 扩展属性 json
    @Property("properties")
    private String properties;
}
