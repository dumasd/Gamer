package com.thinkerwolf.gamer.remoting;


import com.thinkerwolf.gamer.common.concurrent.Promise;

import java.util.concurrent.TimeUnit;

/**
 * 信息交换客户端
 *
 * @author wukai
 * @date 2020/5/11 17:49
 */
public interface ExchangeClient<V> {

    /**
     * 同步发送，直到返回结果
     *
     * @param message 请求信息
     * @return promise
     */
    Promise<V> request(Object message);

    /**
     * 同步发送，等待超时
     *
     * @param message 请求信息
     * @param timeout 请求超时
     * @param unit    超时单位
     * @return promise
     */
    Promise<V> request(Object message, long timeout, TimeUnit unit);
}
