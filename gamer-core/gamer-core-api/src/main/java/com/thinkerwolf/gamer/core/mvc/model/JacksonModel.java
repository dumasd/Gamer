package com.thinkerwolf.gamer.core.mvc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author wukai
 * @since 2020-06-11
 */
public class JacksonModel implements Model<Object> {
    public static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static final String NAME = "json";
    private final Object bean;
    private final byte[] data;

    public JacksonModel(Object bean) {
        this.bean = bean;
        try {
            this.data = objectMapper.writeValueAsBytes(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
