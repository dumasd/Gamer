package com.thinkerwolf.gamer.remoting;

import java.net.SocketAddress;

/**
 * Channel
 *
 * @author wukai
 */
public interface Channel extends Endpoint {

    Object id();

    SocketAddress getLocalAddress();

    SocketAddress getRemoteAddress();

    Object innerCh();

    Object getAttr(String key);

    void setAttr(String key, Object value);
}
