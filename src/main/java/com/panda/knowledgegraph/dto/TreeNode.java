package com.panda.knowledgegraph.dto;

import lombok.Data;

import java.util.List;

@Data
public class TreeNode {
    private Integer id;

    private Integer parentId;

    private String name;

    private String type;

    private String properties;

    private String process;

    private String processProperties;

    private Integer count;

    private List<TreeNode> children;
}
