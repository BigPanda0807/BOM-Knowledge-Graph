package com.panda.knowledgegraph.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResultTreeNode {
    private Long id;

    private Long parentId;

    private String code;

    private String name;

    private String type;

    private String properties;

    private String process;

    private String processProperties;

    private Integer count;

    private List<ResultTreeNode> children;
}
