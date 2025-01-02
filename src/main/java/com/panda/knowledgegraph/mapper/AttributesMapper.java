package com.panda.knowledgegraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.knowledgegraph.entity.Attributes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AttributesMapper extends BaseMapper<Attributes> {

    Attributes getByName(@Param("name") String name);
}
