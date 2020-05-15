package com.thinkerwolf.gamer.rpc;

import java.io.Serializable;

/**
 * RPC响应
 *
 * @author wukai
 * @date 2020/5/15 16:13
 */
public class RpcResponse implements Serializable {

    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
