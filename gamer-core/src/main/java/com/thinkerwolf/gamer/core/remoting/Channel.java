package com.thinkerwolf.gamer.core.remoting;

import java.net.SocketAddress;

/**
 * Channel
 */
public interface Channel extends Endpoint {

    SocketAddress getLocalAddress();

    SocketAddress getRemoteAddress();

    Object innerCh();

}
