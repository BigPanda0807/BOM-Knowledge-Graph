package com.panda.knowledgegraph.contants;

import lombok.AllArgsConstructor;

/**
 * 知识图谱关系类型定义
 */
@AllArgsConstructor
public enum RelationshipEntityTypes {
    // 用户输入结构
    INPUT_CONSIST_OF("input_consist_of"),
    // 成品 半成品 原材料 之间
    CONSIST_OF("consist_of"),
    // 半成品 原材料 设备 与 供应商 之间
    SUPPLY("supply"),
    // 成品 半成品 原材料 与 工艺 之间
    APPLY("apply"),
    // 工艺 与 设备 之间
    USE("use");

    private String type;

    public String getType() {
        return type;
    }
}
