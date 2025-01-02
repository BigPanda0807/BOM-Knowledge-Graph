package com.panda.knowledgegraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.knowledgegraph.entity.Attributes;
import com.panda.knowledgegraph.mapper.AttributesMapper;
import com.panda.knowledgegraph.service.AttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttributesServiceImpl extends ServiceImpl<AttributesMapper, Attributes> implements AttributesService {

    @Autowired
    AttributesMapper attributesMapper;

    @Override
    public Boolean addAttributes(Attributes attributes) {
        if (attributesMapper.getByName(attributes.getName()) == null) {
            return this.save(attributes);
        } else {
            return false;
        }
    }
}
