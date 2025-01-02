package com.panda.knowledgegraph.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InputBomMap {
    List<InputBomNode> bomNodeList;

    List<InputConsistOf> relationshipList;
}
