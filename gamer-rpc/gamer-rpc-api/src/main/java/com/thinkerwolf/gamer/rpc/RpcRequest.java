package com.thinkerwolf.gamer.rpc;

import java.io.Serializable;
import java.util.Map;

/**
 * RPC请求参数
 *
 * @author wukai
 * @date 2020/5/11 17:01
 */
public class RpcRequest implements Serializable {

    private Object[] args;

    /** Rpc attachments */
    private Map<String, String> attachments;

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }
}
