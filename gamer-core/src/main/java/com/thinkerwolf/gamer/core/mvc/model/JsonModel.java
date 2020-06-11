package com.thinkerwolf.gamer.core.mvc.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.thinkerwolf.gamer.common.util.CharsetUtil;

/**
 * JSON数据
 */
public class JsonModel implements Model {

    public static final String NAME = "json";

    private Object bean;

    private byte[] data;

    public JsonModel(Object bean) {
        this.bean = bean;
        String s = JSON.toJSONString(bean);
        this.data = s.getBytes(CharsetUtil.UTF8);
    }

    @Override
    public Object getData() {
        return bean;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public byte[] getBytes() {
        return data;
    }
}
