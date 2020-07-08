package com.thinkerwolf.gamer.rpc;

import java.io.Serializable;

/**
 * RPC请求参数
 *
 * @author wukai
 * @date 2020/5/11 17:01
 */
public class RpcRequest implements Serializable {

    private Object[] args;

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
