package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.remoting.Channel;

/**
 * TCP
 *
 * @author wukai
 */
public class TcpRequest extends AbstractChRequest {

    private final byte[] content;

    public TcpRequest(int requestId, String command, Channel ch, byte[] content, ServletConfig servletConfig) {
        super(requestId, command, ch, servletConfig);
        this.content = content;
        RequestUtil.parseParams(this, content);

        Session session = getSession(false);
        if (session != null) {
            session.setPush(newPush());
        }
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    public Protocol getProtocol() {
        return Protocol.TCP;
    }

    @Override
    public Push newPush() {
        return new TcpPush(getChannel());
    }
}
