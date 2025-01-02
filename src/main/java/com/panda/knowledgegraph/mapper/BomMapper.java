package com.panda.knowledgegraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.knowledgegraph.entity.Bom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BomMapper extends BaseMapper<Bom> {
    Bom findBomByCode(@Param("code") String code);
}
