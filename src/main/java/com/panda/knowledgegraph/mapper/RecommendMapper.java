package com.panda.knowledgegraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.knowledgegraph.entity.Recommend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecommendMapper extends BaseMapper<Recommend> {
    Recommend getByRecommendName(@Param("recommendName") String recommendName);
}
