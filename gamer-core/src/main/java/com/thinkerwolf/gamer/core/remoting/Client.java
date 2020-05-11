package com.thinkerwolf.gamer.core.remoting;

/**
 * Client端
 */
public interface Client extends Endpoint {

    void reconnect() throws RemotingException;

    Channel channel();
}
