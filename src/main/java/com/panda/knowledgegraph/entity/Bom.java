package com.panda.knowledgegraph.entity;

import lombok.Data;

/**
 * 暂时先不用mysql
 */
@Data
public class Bom {

    private Long id;

    private String code;

    private String name;

    private String type;
    // 扩展属性
    private String properties;
}
