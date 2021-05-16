package com.thinkerwolf.gamer.common;

import java.io.Serializable;
import java.util.Map;

/**
 * 被序列化对象
 *
 * @author wukai
 */
public class SerializedObj implements Serializable {

    private int num;
    private Object json;
    private Map<String, Object> map;

    public SerializedObj() {}

    public SerializedObj(int num, Object json) {
        this.num = num;
        this.json = json;
    }

    public SerializedObj(int num, Object json, Map<String, Object> map) {
        this.num = num;
        this.json = json;
        this.map = map;
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

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public String toString() {
        return "SerializedObj{" + "num=" + num + ", json=" + json + ", map=" + map + '}';
    }
}
