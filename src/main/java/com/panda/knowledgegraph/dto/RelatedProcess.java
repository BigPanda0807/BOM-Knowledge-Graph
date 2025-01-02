package com.panda.knowledgegraph.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProcess {

        private Map<String,Object> process;

        private Map<String,Object> sourceBom;

        private Map<String,Object> targetBom;
}
