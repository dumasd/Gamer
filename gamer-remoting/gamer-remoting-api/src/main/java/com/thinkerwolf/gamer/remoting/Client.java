package com.thinkerwolf.gamer.remoting;

/**
 * Client端
 *
 * @author wukai
 */
public interface Client extends Endpoint {
    /**
     * 重新连接
     *
     * @throws RemotingException
     */
    void reconnect() throws RemotingException;

    /**
     * 获取Channel
     *
     * @return channel
     */
    Channel getChannel();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 是否连接在连接状态
     *
     * @return
     */
    boolean isConnected();
}
