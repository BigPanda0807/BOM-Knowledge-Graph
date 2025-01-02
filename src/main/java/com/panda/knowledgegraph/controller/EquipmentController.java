package com.panda.knowledgegraph.controller;

import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.dto.RelatedProcess;
import com.panda.knowledgegraph.entity.Equipment;
import com.panda.knowledgegraph.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/equipment")
public class EquipmentController {

    @Autowired
    EquipmentService equipmentService;

    @GetMapping("/getList")
    public R<List<Equipment>> getList() {
        return R.success(equipmentService.list());
    }

    @PostMapping("/add")
    public R<Boolean> add(@RequestBody Equipment equipment) {
        return R.success(equipmentService.addEquipment(equipment));
    }

    @PostMapping("/edit")
    public R<Boolean> edit(@RequestBody Equipment equipment) {
        return R.success(equipmentService.editEquipment(equipment));
    }

    @GetMapping("/delete")
    public R<Boolean> delete(@RequestParam Long id) {
        return R.success(equipmentService.deleteEquipment(id));
    }

    @GetMapping("/showRelationship")
    public R<List<RelatedProcess>> showRelationship(@RequestParam String code) {
        return R.success(equipmentService.showRelationship(code));
    }

    @GetMapping("/reportException")
    public R<Boolean> reportException(@RequestParam Long id, @RequestParam String code, @RequestParam Integer status) {
        return R.success(equipmentService.reportException(id, code, status));
    }

    @GetMapping("/releaseException")
    public R<Boolean> releaseException(@RequestParam Long id, @RequestParam String code, @RequestParam Integer status) {
        return R.success(equipmentService.releaseException(id, code, status));
    }
}
