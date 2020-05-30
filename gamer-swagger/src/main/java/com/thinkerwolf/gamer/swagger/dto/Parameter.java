package com.thinkerwolf.gamer.swagger.dto;

import com.thinkerwolf.gamer.swagger.schema.ModelRef;
import com.thinkerwolf.gamer.swagger.schema.ModelReference;

import java.io.Serializable;

/**
 * @author wukai
 */
public class Parameter implements Serializable {

    private String name = "";

    private String value = "";

    private String defaultValue = "";

    private boolean required;

    private Class<?> dataTypeClass;

    private String dataType = "";

    private String description = "";

    private String access = "";

    private ModelReference modelRef;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public Class<?> getDataTypeClass() {
        return dataTypeClass;
    }

    public void setDataTypeClass(Class<?> dataTypeClass) {
        this.dataTypeClass = dataTypeClass;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public ModelReference getModelRef() {
        return modelRef;
    }

    public void setModelRef(ModelReference modelRef) {
        this.modelRef = modelRef;
    }
}
