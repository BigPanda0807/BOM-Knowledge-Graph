package com.panda.knowledgegraph.dto;

import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.entity.node.Equipment;
import com.panda.knowledgegraph.entity.node.ManufacturingTechnique;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRelation {
    private ManufacturingTechnique manufacturingTechnique;
    // 与之相关联的设备
    private List<Equipment> equipmentList;

    private Map<String, Object> sourceBom;

    private Map<String, Object> targetBom;
}
