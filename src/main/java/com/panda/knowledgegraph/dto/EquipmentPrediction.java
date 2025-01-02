package com.panda.knowledgegraph.dto;

import com.panda.knowledgegraph.entity.node.ManufacturingTechnique;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentPrediction {

    private ManufacturingTechnique manufacturingTechnique;

    private Map<String, Object> sourceBom;

    private Map<String, Object> targetBom;

    private List<ProcessRelation> processRelations;
}
