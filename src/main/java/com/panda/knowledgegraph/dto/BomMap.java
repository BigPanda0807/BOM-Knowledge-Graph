package com.panda.knowledgegraph.dto;

import com.panda.knowledgegraph.entity.node.BomNode;
import com.panda.knowledgegraph.entity.relationship.ConsistOf;
import lombok.Data;

import java.util.List;

@Data
public class BomMap {
    List<BomNode> bomNodeList;

    List<ConsistOf> relationshipList;
}
