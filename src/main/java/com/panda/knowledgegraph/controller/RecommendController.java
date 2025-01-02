package com.panda.knowledgegraph.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.panda.knowledgegraph.common.R;
import com.panda.knowledgegraph.dto.BomMap;
import com.panda.knowledgegraph.dto.InputBomMap;
import com.panda.knowledgegraph.dto.Mapping;
import com.panda.knowledgegraph.entity.Recommend;
import com.panda.knowledgegraph.service.RecommendService;
import com.panda.knowledgegraph.vo.InputRecommendVO;
import com.panda.knowledgegraph.vo.RecommendResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/recommend")
public class RecommendController {

    @Autowired
    RecommendService recommendService;

    // 将用户输入结构存到图数据库中
    @GetMapping("/doInput")
    public R<Boolean> doInput(Long id) throws JsonProcessingException {
        recommendService.importUserInputToNeo4j(id);
        return R.success(true);
    }

    // 用户推荐结果丰富知识图谱
    @GetMapping("/recommendResultReturnToKG")
    public R<Boolean> recommendResultReturnToKG(Long id) throws JsonProcessingException {
        recommendService.recommendResultReturnToKG(id);
        return R.success(true);
    }

    // 以图的方式回显用户输入结构
    @GetMapping("/getInput")
    public R<InputBomMap> getInput(Long id) {
        return R.success(recommendService.getInput(id));
    }

    // 获取结构相似的推荐
    @GetMapping("/getStructureRecommend")
    public R<List<Mapping>> getStructureRecommend(Long id) {
        return R.success(recommendService.getStructureRecommend(id));
    }

    // 获取结构相似的成品
    @GetMapping("/getRecommendFinishedProduct")
    public R<String> getRecommendFinishedProduct(String code) throws JsonProcessingException {
        return recommendService.getRecommendFinishedProduct(code);
    }

    // 更新用户输入结构
    @PostMapping("/input/update")
    public R<Boolean> updateInput(@RequestBody InputRecommendVO inputRecommendVO) {
        Recommend recommend = new Recommend();
        recommend.setId(inputRecommendVO.getId());
        // 推荐名不可修改
//        recommend.setRecommendName(inputRecommendVO.getRecommendName());
        recommend.setUserInputStructure(inputRecommendVO.getUserInputStructure());
        recommend.setUserInputNodeCount(inputRecommendVO.getUserInputNodeCount());
        recommend.setStartId(inputRecommendVO.getStartId());
        return R.success(recommendService.updateById(recommend));
    }

    // 更新用户推荐结果
    @PostMapping("/updateRecommendResult")
    public R<Boolean> updateRecommendResult(@RequestBody RecommendResultVO recommendResultVO) {
        Recommend recommend = new Recommend();
        recommend.setId(recommendResultVO.getId());
        recommend.setRecommendResult(recommendResultVO.getRecommendResult());
        return R.success(recommendService.updateById(recommend));
    }

    // RecommendList页新增推荐
    @GetMapping("/add")
    public R<Boolean> add(@RequestParam("recommendName") String recommendName) {
        return R.success(recommendService.addRecommend(recommendName));
    }

    // RecommendList页删除推荐
    @GetMapping("/delete")
    public R<Boolean> delete(@RequestParam("id") Long id) {
        return R.success(recommendService.deleteRecommend(id));
    }

    // 获取推荐详情
    @GetMapping("/getRecommendById")
    public R<Recommend> getRecommendById(@RequestParam("id") Long id) {
        return R.success(recommendService.getById(id));
    }

    // 获取RecommendList所有推荐
    @GetMapping("/getList")
    public R<List<Recommend>> getList() {
        return R.success(recommendService.list());
    }
}
