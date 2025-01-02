package com.panda.knowledgegraph.controller;

import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.entity.Attributes;
import com.panda.knowledgegraph.entity.ManufacturingTechnique;
import com.panda.knowledgegraph.service.AttributesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/attributes")
public class AttributesController {

    @Autowired
    AttributesService attributesService;

    @GetMapping("/getList")
    public R<List<Attributes>> getList() {
        return R.success(attributesService.list());
    }

    @PostMapping("/add")
    public R<Boolean> add(@RequestBody Attributes attributes) {
        return R.success(attributesService.addAttributes(attributes));
    }

    @PostMapping("/edit")
    public R<Boolean> edit(@RequestBody Attributes attributes) {
        return R.success(attributesService.updateById(attributes));
    }

    @GetMapping("/delete")
    public R<Boolean> delete(@RequestParam Long id) {
        return R.success(attributesService.removeById(id));
    }
}
