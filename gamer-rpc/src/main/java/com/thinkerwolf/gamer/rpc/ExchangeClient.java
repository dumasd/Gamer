package com.thinkerwolf.gamer.rpc;


import com.thinkerwolf.gamer.common.concurrent.Promise;

import java.util.concurrent.TimeUnit;

/**
 * RPC信息交换客户端
 *
 * @author wukai
 * @date 2020/5/11 17:49
 */
public interface ExchangeClient {

    /**
     * 同步发送，直到返回结果
     *
     * @param message
     * @return
     */
    Promise request(Object message);

    /**
     * 同步发送，等待超时
     *
     * @param message
     * @param timeout
     * @param unit
     * @return
     */
    Promise request(Object message, long timeout, TimeUnit unit);
}
