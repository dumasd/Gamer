package com.thinkerwolf.gamer.remoting;

import com.thinkerwolf.gamer.common.URL;

/**
 * 网络中断
 */
public interface Endpoint extends AutoCloseable {


    /**
     * 获取地址
     *
     * @return
     */
    URL getUrl();

    void send(Object message, boolean sent) throws RemotingException;

    void send(Object message) throws RemotingException;

    @Override
    void close();

    boolean isClosed();

}
