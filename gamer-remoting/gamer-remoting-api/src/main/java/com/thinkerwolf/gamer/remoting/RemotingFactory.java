package com.thinkerwolf.gamer.remoting;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.common.URL;

@SPI("netty")
public interface RemotingFactory {

    /**
     * 新建 server
     *
     * @param url
     * @param handlers
     * @return
     * @throws Exception
     */
    Server newServer(URL url, ChannelHandler... handlers) throws Exception;

    /**
     * 新建Client
     *
     * @param url
     * @param handlers
     * @return
     * @throws Exception
     */
    Client newClient(URL url, ChannelHandler... handlers) throws Exception;
}
