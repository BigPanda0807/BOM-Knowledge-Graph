package com.panda.knowledgegraph.vo;

import lombok.Data;

@Data
public class InputRecommendVO {
    private Long id;
    //    private String recommendName;
    private String userInputStructure;
    private Integer userInputNodeCount;
    private Integer startId;
}
