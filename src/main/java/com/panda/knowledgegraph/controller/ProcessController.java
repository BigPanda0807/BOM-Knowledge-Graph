package com.panda.knowledgegraph.controller;

import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.entity.ManufacturingTechnique;
import com.panda.knowledgegraph.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    ProcessService processService;

    @GetMapping("/getList")
    public R<List<ManufacturingTechnique>> getList() {
        return R.success(processService.list());
    }

    @PostMapping("/add")
    public R<Boolean> add(@RequestBody ManufacturingTechnique process) {
        return R.success(processService.addProcess(process));
    }

    @PostMapping("/edit")
    public R<Boolean> edit(@RequestBody ManufacturingTechnique process) {
        return R.success(processService.editProcess(process));
    }

    @GetMapping("/delete")
    public R<Boolean> delete(@RequestParam Long id) {
        return R.success(processService.deleteProcess(id));
    }

    @PostMapping("/predictEquipment")
    public R<List<String>> predictEquipment(@RequestBody com.panda.knowledgegraph.entity.node.ManufacturingTechnique process) {
        return R.success(processService.predictEquipment(process));
    }
}
