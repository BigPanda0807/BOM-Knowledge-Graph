package com.panda.knowledgegraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.knowledgegraph.entity.Attributes;

public interface AttributesService  extends IService<Attributes> {
    Boolean addAttributes(Attributes attributes);
}
