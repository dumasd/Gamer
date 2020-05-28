package com.thinkerwolf.gamer.swagger.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Api操作集合
 *
 * @author wukai
 */
public class ApiDescriptor implements Serializable {

    private String path;

    private List<Operation> operations;

    public ApiDescriptor(String path, List<Operation> operations) {
        this.path = path;
        this.operations = operations;
    }

    public String getPath() {
        return path;
    }

    public List<Operation> getOperations() {
        return operations;
    }

}
