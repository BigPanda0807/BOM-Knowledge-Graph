package com.panda.knowledgegraph.contants;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 知识图谱实体类型定义
 */
@AllArgsConstructor
public enum NodeEntityTypes {
    // 用户输入bom
    INPUT_BOM("inputBom"),
    // bom
    BOM("bom"),
    // 零部件（原材料）
    COMPONENT("component"),
    // 半成品（中间产品）
    SEMIFINISHEDPRODUCT("semiFinishedProduct"),
    // 成品（产品）
    FINISHEDPRODUCT("finishedProduct"),
    // 供应商
    SUPPLIER("supplier"),
    // 设备
    EQUIPMENT("equipment"),
    // 工艺
    MANUFACTURINGTECHNIQUE("manufacturingTechnique");

    private String type;

    public String getType() {
        return type;
    }
}
