package com.panda.knowledgegraph.dto;

import com.panda.knowledgegraph.entity.node.BomNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Mapping {
    private List<BomNode> bomNodeList;

    private List<Map<String, Object>> relationshipList;

    private Long parentId;
}
