package com.thinkerwolf.gamer.rpc;

import java.io.Serializable;
import java.util.Map;

/**
 * RPC Response
 *
 * @author wukai
 * @date 2020/5/15 16:13
 */
public class RpcResponse implements Serializable {

    private int requestId;
    /** Rpc correct result */
    private Object result;

    private Map<String, String> attachments;
    /** Rpc exception */
    private Throwable tx;

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getTx() {
        return tx;
    }

    public void setTx(Throwable tx) {
        this.tx = tx;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }
}
