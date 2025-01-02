package com.panda.knowledgegraph.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.knowledgegraph.dao.BomNodeRepository;
import com.panda.knowledgegraph.dao.EquipmentRepository;
import com.panda.knowledgegraph.dto.RelatedProcess;
import com.panda.knowledgegraph.entity.Equipment;
import com.panda.knowledgegraph.entity.ManufacturingTechnique;
import com.panda.knowledgegraph.mapper.EquipmentMapper;
import com.panda.knowledgegraph.mapper.ProcessMapper;
import com.panda.knowledgegraph.service.EquipmentService;
import com.panda.knowledgegraph.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Autowired
    EquipmentMapper equipmentMapper;

    @Autowired
    ProcessMapper processMapper;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    BomNodeRepository bomNodeRepository;

    @Autowired
    ProcessService processService;

    @Override
    public Boolean addEquipment(Equipment equipment) {
        // 首先看是否已存在
        if (equipmentMapper.getEquipmentByCode(equipment.getCode()) == null) {
            // 图数据库新增
            equipmentRepository.mergeEquipment(equipment.getCode(), equipment.getName(), equipment.getType(), equipment.getStatus(), equipment.getTechnicalSpecifications(), equipment.getProperties());
            if (equipment.getRelatedProcess() == null) {
                equipment.setRelatedProcess("[]");
            } else {
                JSONArray processStrArray = JSON.parseArray(equipment.getRelatedProcess());
                String[] processCodeArray = processStrArray.toArray(new String[0]);
                for (String code : processCodeArray) {
                    // 图数据库创建关系
                    bomNodeRepository.mergeEquipmentAndManufacturingTechniqueRelationship(equipment.getCode(), code);
                    ManufacturingTechnique process = processMapper.getProcessByCode(code);
                    // 新增设备的时候选择关联了工艺 则更新工艺所关联的设备
                    if (process.getRelatedEquipment() == null) {
                        processMapper.updateProcessRelatedEquipment(code, "[\"" + equipment.getCode() + "\"]");
                    } else {
                        JSONArray jsonArray = JSON.parseArray(process.getRelatedEquipment());
                        String[] CodeArray = jsonArray.toArray(new String[0]);
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(CodeArray));
                        list.add(equipment.getCode());
                        String[] newCodeArray = list.toArray(new String[0]);
                        processMapper.updateProcessRelatedEquipment(code, JSON.toJSONString(newCodeArray));
                    }
                }
            }
            return this.save(equipment);
        } else {
            return false;
        }
    }

    @Override
    public Boolean deleteEquipment(Long id) {
        Equipment equipment = getById(id);
        equipmentRepository.deleteEquipmentByCode(equipment.getCode());
        JSONArray processStrArray = JSON.parseArray(equipment.getRelatedProcess());
        String[] processCodeArray = processStrArray.toArray(new String[0]);
        if (processCodeArray.length > 0) {
            for (String processCode : processCodeArray) {
                ManufacturingTechnique process = processMapper.getProcessByCode(processCode);
                processMapper.updateProcessRelatedEquipment(processCode, removeEquipment(process.getRelatedEquipment(), equipment.getCode()));
            }
        }
        return this.removeById(id);
    }

    @Override
    public Boolean editEquipment(Equipment equipment) {
        Equipment oldEquipment = this.getById(equipment.getId());
        JSONArray oldProcessStrArray = JSON.parseArray(oldEquipment.getRelatedProcess());
        String[] oldProcessCodeArray = oldProcessStrArray.toArray(new String[0]);
        for (String processCode : oldProcessCodeArray) {
            bomNodeRepository.deleteEAndMRelationship(equipment.getCode(), processCode);
            ManufacturingTechnique process = processMapper.getProcessByCode(processCode);
            processMapper.updateProcessRelatedEquipment(processCode, removeEquipment(process.getRelatedEquipment(), equipment.getCode()));
        }
        JSONArray newProcessStrArray = JSON.parseArray(equipment.getRelatedProcess());
        String[] newProcessCodeArray = newProcessStrArray.toArray(new String[0]);
        for (String code : newProcessCodeArray) {
            bomNodeRepository.mergeEquipmentAndManufacturingTechniqueRelationship(equipment.getCode(), code);
            ManufacturingTechnique process = processMapper.getProcessByCode(code);
            if (process.getRelatedEquipment() == null) {
                processMapper.updateProcessRelatedEquipment(code, "[\"" + equipment.getCode() + "\"]");
            } else {
                JSONArray jsonArray = JSON.parseArray(process.getRelatedEquipment());
                String[] codeArray = jsonArray.toArray(new String[0]);
                ArrayList<String> list = new ArrayList<>(Arrays.asList(codeArray));
                list.add(equipment.getCode());
                String[] newCodeArray = list.toArray(new String[0]);
                processMapper.updateProcessRelatedEquipment(code, JSON.toJSONString(newCodeArray));
            }
        }
        equipmentRepository.updateEquipmentByCode(equipment.getCode(), equipment.getName(), equipment.getType(), equipment.getStatus(), equipment.getTechnicalSpecifications(), equipment.getProperties());
        return this.updateById(equipment);
    }

    @Override
    public List<RelatedProcess> showRelationship(String code) {
        List<RelatedProcess> resp = new ArrayList<>();
        List<Map<String, Object>> equipmentRelatedManufacturingTechnique = equipmentRepository.findEquipmentRelatedManufacturingTechnique(code);
        for (Map<String, Object> map : equipmentRelatedManufacturingTechnique) {
            RelatedProcess relatedProcess = new RelatedProcess();
            relatedProcess.setProcess(map);
            relatedProcess.setSourceBom(equipmentRepository.findProcessRelatedSourceBom(map.get("code").toString()).get(0));
            relatedProcess.setTargetBom(equipmentRepository.findProcessRelatedTargetBom(map.get("code").toString()).get(0));
            resp.add(relatedProcess);
        }
        return resp;
    }

    @Override
    public Boolean updateEquipmentStatus(Long id, String code, Integer status) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setCode(code);
        equipment.setStatus(status);
        equipmentRepository.updateEquipmentStatus(code, status);
        return this.updateById(equipment);
    }

    public void updateProcessStatus(String code, Integer status) {
        // 查找到与之关联的工艺 修改工艺状态为异常
        List<Map<String, Object>> relatedProcessList = equipmentRepository.findEquipmentRelatedManufacturingTechnique(code);
        for (Map<String, Object> map : relatedProcessList) {
            processService.updateProcessStatus(map.get("code").toString(), status);
            // 找到targetBom
            List<Map<String, Object>> targetList = new ArrayList<>();
            targetList.add(equipmentRepository.findProcessRelatedTargetBom(map.get("code").toString()).get(0));
            while (!targetList.isEmpty()) {
                List<Map<String, Object>> newTargetList = new ArrayList<>();
                for (Map<String, Object> target : targetList) {
                    // 找到与之关联的工艺
                    List<Map<String, Object>> processList = equipmentRepository.findTargetBomRelatedProcess(target.get("code").toString());
                    for (Map<String, Object> process : processList) {
                        processService.updateProcessStatus(process.get("code").toString(), status);
                        newTargetList.add(equipmentRepository.findProcessRelatedTargetBom(process.get("code").toString()).get(0));
                    }
                }
                targetList = newTargetList;
            }
        }
    }

    @Override
    public Boolean reportException(Long id, String code, Integer status) {
        updateProcessStatus(code, status);
        return this.updateEquipmentStatus(id, code, status);
    }

    @Override
    public Boolean releaseException(Long id, String code, Integer status) {
        updateProcessStatus(code, status);
        return this.updateEquipmentStatus(id, code, status);
    }

    public String removeEquipment(String array, String equipment) {
        JSONArray strArray = JSON.parseArray(array);
        String[] strings = strArray.toArray(new String[0]);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(strings));
        list.remove(equipment);
        String[] newArray = list.toArray(new String[0]);
        return JSON.toJSONString(newArray);
    }
}
