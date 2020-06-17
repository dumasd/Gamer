package com.thinkerwolf.gamer.remoting;

import java.net.SocketAddress;

/**
 * Channel
 */
public interface Channel extends Endpoint {

    Object id();

    SocketAddress getLocalAddress();

    SocketAddress getRemoteAddress();

    Object innerCh();

}