package com.thinkerwolf.gamer.common;

import java.io.Serializable;

public class SerializetionObj implements Serializable {

    private int num;
    private Object json;

    public SerializetionObj() {
    }

    public SerializetionObj(int num, Object json) {
        this.num = num;
        this.json = json;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public Object getJson() {
        return json;
    }

    public void setJson(Object json) {
        this.json = json;
    }
}
