package com.panda.knowledgegraph.dto;

import lombok.Data;

import java.util.List;

@Data
public class FinishedProductTreeNode {
    private Long id;

    private Long parentId;

    private String code;

    private String parentCode;

    private String name;

    private String type;

    private String properties;

    private String process;

    private String processProperties;

    private Long count;

    private List<FinishedProductTreeNode> children;
}
