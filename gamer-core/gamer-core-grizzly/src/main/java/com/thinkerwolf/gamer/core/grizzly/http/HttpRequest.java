package com.thinkerwolf.gamer.core.grizzly.http;

import com.thinkerwolf.gamer.core.servlet.AbstractChRequest;
import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;

public class HttpRequest extends AbstractChRequest {

    public HttpRequest(int requestId, String command, Channel ch, ServletConfig servletConfig) {
        super(requestId, command, ch, servletConfig);
    }

    @Override
    public byte[] getContent() {
        return new byte[0];
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Push newPush() {
        return null;
    }
}
