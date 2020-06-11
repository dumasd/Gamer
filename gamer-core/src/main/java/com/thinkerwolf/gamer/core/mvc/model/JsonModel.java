package com.thinkerwolf.gamer.core.mvc.model;

/**
 * JSON数据
 */
public class JsonModel extends JacksonModel {

    public static final String NAME = "json";

    public JsonModel(Object bean) {
       super(bean);
    }

}
