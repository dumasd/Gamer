package com.thinkerwolf.gamer.common;

import java.io.Serializable;

/**
 * 被序列化对象
 *
 * @author wukai
 */
public class SerializedObj implements Serializable {

    private int num;
    private Object json;
    //    private Object[] args;

    public SerializedObj() {}

    public SerializedObj(int num, Object json) {
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

    //    public Object[] getArgs() {
    //        return args;
    //    }
    //
    //    public void setArgs(Object[] args) {
    //        this.args = args;
    //    }
}
