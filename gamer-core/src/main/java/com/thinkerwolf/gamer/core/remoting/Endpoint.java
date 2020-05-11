package com.thinkerwolf.gamer.core.remoting;

import com.thinkerwolf.gamer.common.URL;

/**
 * 网络中断
 */
public interface Endpoint {

    /**
     * 获取地址
     *
     * @return
     */
    URL getUrl();

    void send(Object message, boolean sent) throws RemotingException;

    void send(Object message) throws RemotingException;

    void close();

    boolean isClosed();

}
