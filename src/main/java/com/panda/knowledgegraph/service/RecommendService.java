package com.panda.knowledgegraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.dto.InputBomMap;
import com.panda.knowledgegraph.dto.Mapping;
import com.panda.knowledgegraph.entity.Recommend;

import java.util.List;

public interface RecommendService extends IService<Recommend> {
    public void importUserInputToNeo4j(Long id) throws JsonProcessingException;

    public InputBomMap getInput(Long id);

    public boolean addRecommend(String recommendName);

    public boolean deleteRecommend(Long id);

    public List<Mapping> getStructureRecommend(Long id);

    public R<String> getRecommendFinishedProduct(String code) throws JsonProcessingException;

    void recommendResultReturnToKG(Long id) throws JsonProcessingException;
}
