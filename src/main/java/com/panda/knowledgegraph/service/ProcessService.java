package com.panda.knowledgegraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.knowledgegraph.entity.ManufacturingTechnique;

import java.util.List;

public interface ProcessService extends IService<ManufacturingTechnique> {

    Boolean addProcess(ManufacturingTechnique equipment);

    Boolean deleteProcess(Long id);

    Boolean editProcess(ManufacturingTechnique process);

    List<String> predictEquipment(com.panda.knowledgegraph.entity.node.ManufacturingTechnique process);

    Boolean updateProcessStatus(String code, Integer status);
}
