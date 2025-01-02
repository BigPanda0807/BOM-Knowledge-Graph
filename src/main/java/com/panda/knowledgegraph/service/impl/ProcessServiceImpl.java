package com.panda.knowledgegraph.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.knowledgegraph.dao.BomNodeRepository;
import com.panda.knowledgegraph.dao.EquipmentRepository;
import com.panda.knowledgegraph.dao.ManufacturingTechniqueRepository;
import com.panda.knowledgegraph.dto.EquipmentPrediction;
import com.panda.knowledgegraph.dto.ProcessRelation;
import com.panda.knowledgegraph.entity.Equipment;
import com.panda.knowledgegraph.entity.ManufacturingTechnique;
import com.panda.knowledgegraph.mapper.EquipmentMapper;
import com.panda.knowledgegraph.mapper.ProcessMapper;
import com.panda.knowledgegraph.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, ManufacturingTechnique> implements ProcessService {

    @Autowired
    ProcessMapper processMapper;

    @Autowired
    EquipmentMapper equipmentMapper;

    @Autowired
    ManufacturingTechniqueRepository manufacturingTechniqueRepository;

    @Autowired
    EquipmentRepository equipmentRepository;

    @Autowired
    BomNodeRepository bomNodeRepository;

    @Override
    public Boolean addProcess(ManufacturingTechnique process) {
        if (processMapper.getProcessByCode(process.getCode()) == null) {
            manufacturingTechniqueRepository.mergeManufacturingTechnique(process.getCode(), process.getName(), process.getStatus(), process.getDescription(), process.getProcedures(), process.getParameter(), process.getProperties());
            if (process.getRelatedEquipment() == null) {
                process.setRelatedEquipment("[]");
            } else {
                JSONArray equipmentStrArray = JSON.parseArray(process.getRelatedEquipment());
                String[] equipmentCodeArray = equipmentStrArray.toArray(new String[0]);
                for (String code : equipmentCodeArray) {
                    bomNodeRepository.mergeEquipmentAndManufacturingTechniqueRelationship(code, process.getCode());
                    Equipment equipment = equipmentMapper.getEquipmentByCode(code);
                    if (equipment.getRelatedProcess() == null) {
                        equipmentMapper.updateEquipmentRelatedProcess(code, "[\"" + process.getCode() + "\"]");
                    } else {
                        JSONArray jsonArray = JSON.parseArray(equipment.getRelatedProcess());
                        String[] CodeArray = jsonArray.toArray(new String[0]);
                        ArrayList<String> list = new ArrayList<>(Arrays.asList(CodeArray));
                        list.add(process.getCode());
                        String[] newCodeArray = list.toArray(new String[0]);
                        equipmentMapper.updateEquipmentRelatedProcess(code, JSON.toJSONString(newCodeArray));
                    }
                }
            }
            if (process.getSourceBomCode() != null && process.getTargetBomCode() != null) {
                bomNodeRepository.mergeBomAndManufacturingTechniqueRelationship(process.getSourceBomCode(), process.getTargetBomCode(), process.getCode());
            }
            return this.save(process);
        } else {
            return false;
        }
    }

    @Override
    public Boolean deleteProcess(Long id) {
        ManufacturingTechnique process = getById(id);
        manufacturingTechniqueRepository.deleteProcessByCode(process.getCode());
        JSONArray equipmentStrArray = JSON.parseArray(process.getRelatedEquipment());
        String[] equipmentCodeArray = equipmentStrArray.toArray(new String[0]);
        if (equipmentCodeArray.length > 0) {
            for (String equipmentCode : equipmentCodeArray) {
                Equipment equipment = equipmentMapper.getEquipmentByCode(equipmentCode);
                equipmentMapper.updateEquipmentRelatedProcess(equipmentCode, removeProcess(equipment.getRelatedProcess(), process.getCode()));
            }
        }
        return this.removeById(id);
    }

    @Override
    public Boolean editProcess(ManufacturingTechnique process) {
        ManufacturingTechnique oldProcess = this.getById(process.getId());
        JSONArray oldEquipmentStrArray = JSON.parseArray(oldProcess.getRelatedEquipment());
        String[] oldEquipmentCodeArray = oldEquipmentStrArray.toArray(new String[0]);
        if (oldEquipmentCodeArray.length > 0) {
            for (String equipmentCode : oldEquipmentCodeArray) {
                bomNodeRepository.deleteEAndMRelationship(equipmentCode, process.getCode());
                Equipment equipment = equipmentMapper.getEquipmentByCode(equipmentCode);
                equipmentMapper.updateEquipmentRelatedProcess(equipmentCode, removeProcess(equipment.getRelatedProcess(), process.getCode()));
            }
        }
        JSONArray newEquipmentStrArray = JSON.parseArray(process.getRelatedEquipment());
        String[] newEquipmentCodeArray = newEquipmentStrArray.toArray(new String[0]);
        if (newEquipmentCodeArray.length > 0) {
            for (String code : newEquipmentCodeArray) {
                bomNodeRepository.mergeEquipmentAndManufacturingTechniqueRelationship(code, process.getCode());
                Equipment equipment = equipmentMapper.getEquipmentByCode(code);
                if (equipment.getRelatedProcess() == null) {
                    equipmentMapper.updateEquipmentRelatedProcess(code, "[\"" + process.getCode() + "\"]");
                } else {
                    JSONArray jsonArray = JSON.parseArray(equipment.getRelatedProcess());
                    String[] codeArray = jsonArray.toArray(new String[0]);
                    ArrayList<String> list = new ArrayList<>(Arrays.asList(codeArray));
                    list.add(process.getCode());
                    String[] newCodeArray = list.toArray(new String[0]);
                    equipmentMapper.updateEquipmentRelatedProcess(code, JSON.toJSONString(newCodeArray));
                }
            }
        }
        if (oldProcess.getSourceBomCode() != null && !oldProcess.getSourceBomCode().equals("") && oldProcess.getTargetBomCode() != null && !oldProcess.getTargetBomCode().equals("")) {
            bomNodeRepository.deleteMAndBRelationship(oldProcess.getSourceBomCode(), oldProcess.getTargetBomCode(), oldProcess.getCode());
            // 以防没有关系了

        }
        if (process.getSourceBomCode() != null && !process.getSourceBomCode().equals("") && process.getTargetBomCode() != null && !process.getTargetBomCode().equals("")) {
            // 删除已有的关系
            bomNodeRepository.mergeBomAndManufacturingTechniqueRelationship(process.getSourceBomCode(), process.getTargetBomCode(), process.getCode());
        }
        manufacturingTechniqueRepository.updateProcessByCode(process.getCode(), process.getName(), process.getDescription(), process.getParameter(), process.getProcedures(), process.getStatus(), process.getProperties());
        return this.updateById(process);
    }

    @Override
    public List<String> predictEquipment(com.panda.knowledgegraph.entity.node.ManufacturingTechnique process) {
        EquipmentPrediction equipmentPrediction = new EquipmentPrediction();
        equipmentPrediction.setSourceBom(equipmentRepository.findProcessRelatedSourceBom(process.getCode()).get(0));
        equipmentPrediction.setTargetBom(equipmentRepository.findProcessRelatedTargetBom(process.getCode()).get(0));
        // 首先查找到所有的工艺
        List<ProcessRelation> processRelations = new ArrayList<>();
        List<com.panda.knowledgegraph.entity.node.ManufacturingTechnique> allManufacturingTechnique = manufacturingTechniqueRepository.findAllManufacturingTechnique();
        for (com.panda.knowledgegraph.entity.node.ManufacturingTechnique manufacturingTechnique : allManufacturingTechnique) {
            ProcessRelation processRelation = new ProcessRelation();
            processRelation.setManufacturingTechnique(manufacturingTechnique);
            List<com.panda.knowledgegraph.entity.node.Equipment> equipmentList = new ArrayList<>();
            List<Map<String, Object>> relatedEquipment = manufacturingTechniqueRepository.findRelatedEquipmentByCode(manufacturingTechnique.getCode());
            for (Map<String, Object> equipment : relatedEquipment) {
                com.panda.knowledgegraph.entity.node.Equipment newEquipment = new com.panda.knowledgegraph.entity.node.Equipment();
                newEquipment.setCode((String) equipment.get("code"));
                newEquipment.setName((String) equipment.get("name"));
                newEquipment.setType((String) equipment.get("type"));
                newEquipment.setProperties((String) equipment.get("properties"));
                newEquipment.setStatus(Integer.parseInt(equipment.get("status").toString()));
                newEquipment.setTechnicalSpecifications((String) equipment.get("technicalSpecifications"));
                equipmentList.add(newEquipment);
            }
            // 查找到与之关联的所有设备
            processRelation.setEquipmentList(equipmentList);
            processRelation.setSourceBom(equipmentRepository.findProcessRelatedSourceBom(manufacturingTechnique.getCode()).get(0));
            processRelation.setTargetBom(equipmentRepository.findProcessRelatedTargetBom(manufacturingTechnique.getCode()).get(0));
            processRelations.add(processRelation);
        }
        equipmentPrediction.setProcessRelations(processRelations);
        equipmentPrediction.setManufacturingTechnique(process);
        String jsonData = JSON.toJSONString(equipmentPrediction);
        RestTemplate restTemplate = new RestTemplate();
        // 设置请求头为 application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonData, headers);
        String url = "http://localhost:5000/predict/equipment";
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestEntity, String.class);
        // 获取响应状态码和响应体
        HttpStatus statusCode = responseEntity.getStatusCode();
        String responseBody = responseEntity.getBody();
        System.out.println("Response Status: " + statusCode);
        System.out.println("Response Body: " + responseBody);
        return null;
    }

    public String removeProcess(String array, String process) {
        JSONArray strArray = JSON.parseArray(array);
        String[] strings = strArray.toArray(new String[0]);
        ArrayList<String> list = new ArrayList<>(Arrays.asList(strings));
        list.remove(process);
        String[] newArray = list.toArray(new String[0]);
        return JSON.toJSONString(newArray);
    }

    public Boolean updateProcessStatus(String code, Integer status) {
        manufacturingTechniqueRepository.updateEquipmentStatus(code, status);
        return processMapper.updateStatusByCode(code, status);
    }
}
