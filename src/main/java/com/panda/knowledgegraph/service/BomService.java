package com.panda.knowledgegraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.dto.BomMap;
import com.panda.knowledgegraph.entity.Bom;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BomService extends IService<Bom> {

    R<String> uploadAndSaveToNeo4j(MultipartFile file) throws IOException;

    R<BomMap> renderMap();

    R<List<Bom>> getAllBom();

    R<String> getFinishedProductNameByCode(String code) throws JsonProcessingException;

    R<BomMap> getBomNodeAndRelationship(String code);
}
