package com.panda.knowledgegraph.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.panda.knowledgegraph.entity.Equipment;
import com.panda.knowledgegraph.entity.Recommend;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
    Equipment getEquipmentByCode(@Param("code") String code);

    Integer updateEquipmentRelatedProcess(String code, String process);
}
