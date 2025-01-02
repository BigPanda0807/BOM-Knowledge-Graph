package com.panda.knowledgegraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.knowledgegraph.dto.RelatedProcess;
import com.panda.knowledgegraph.entity.Equipment;

import java.util.List;

public interface EquipmentService extends IService<Equipment> {
    Boolean addEquipment(Equipment equipment);

    Boolean deleteEquipment(Long id);

    Boolean editEquipment(Equipment equipment);

    List<RelatedProcess> showRelationship(String code);

    Boolean updateEquipmentStatus(Long id, String code, Integer status);

    Boolean reportException(Long id, String code, Integer status);

    Boolean releaseException(Long id, String code, Integer status);

}
