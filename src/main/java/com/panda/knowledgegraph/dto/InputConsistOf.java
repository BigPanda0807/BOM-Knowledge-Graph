package com.panda.knowledgegraph.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputConsistOf {
    private Long rid;
    private String type;
    private Integer quantity;
    private Long sourceId;
    private Long targetId;
    // 所有属性 包括扩展属性
    private String properties;
}
