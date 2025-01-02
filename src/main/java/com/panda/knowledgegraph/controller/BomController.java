package com.panda.knowledgegraph.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.dto.BomMap;
import com.panda.knowledgegraph.entity.Bom;
import com.panda.knowledgegraph.service.BomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bom")
public class BomController {

    @Autowired
    private BomService bomService;

    @PostMapping("/upload")
    public R<String> uploadAndSaveToNeo4j(MultipartFile file) throws IOException {
        return bomService.uploadAndSaveToNeo4j(file);
    }

    @GetMapping("/getAllBom")
    public R<List<Bom>> getAllBom() {
        return bomService.getAllBom();
    }

    @GetMapping("/getFinishedProductByCode")
    public R<String> getFinishedProductNameByCode(@RequestParam("code") String code) throws JsonProcessingException {
        return bomService.getFinishedProductNameByCode(code);
    }

    @GetMapping("/getBomNodeAndRelationship")
    public R<BomMap> getBomNodeAndRelationship(@RequestParam("code") String code) {
        return bomService.getBomNodeAndRelationship(code);
    }

    @GetMapping("/renderMap")
    public R<BomMap> renderMap() {
        return bomService.renderMap();
    }
}
