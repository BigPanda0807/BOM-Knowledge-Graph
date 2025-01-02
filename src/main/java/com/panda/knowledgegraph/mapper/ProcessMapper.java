package com.panda.knowledgegraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.knowledgegraph.entity.ManufacturingTechnique;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProcessMapper extends BaseMapper<ManufacturingTechnique> {
    ManufacturingTechnique getProcessByCode(String code);

    Integer updateProcessRelatedEquipment(String code, String equipment);

    Boolean updateStatusByCode(@Param("code") String code, @Param("status") Integer status);
}
