package com.thinkerwolf.gamer.core.mvc.model;

import com.thinkerwolf.gamer.core.mvc.FreemarkerHelper;

import java.util.Map;

public class FreemarkerModel implements Model<Map<String, Object>> {
    /**
     * freemarker template
     */
    private String template;

    private Map<String, Object> data;

    public FreemarkerModel(String template, Map<String, Object> data) {
        this.template = template;
        this.data = data;
    }

    @Override
    public Map<String, Object> getData() {
        return null;
    }

    @Override
    public String name() {
        return "freemarker";
    }

    @Override
    public byte[] getBytes() {
        return FreemarkerHelper.getTemplateBytes(template, data);
    }
}
