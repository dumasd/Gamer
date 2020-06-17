package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.netty.AbstractServletHandler;
import com.thinkerwolf.gamer.netty.NettyChannel;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.util.AttributeKey;

public class HttpServletHandler extends AbstractServletHandler {

    public HttpServletHandler(URL url) {
        super(url);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        io.netty.handler.codec.http.HttpRequest nettyRequest = (io.netty.handler.codec.http.HttpRequest) message;
        io.netty.channel.Channel nettyChannel = ((NettyChannel) channel).innerCh();
        final Response response = new HttpResponse(nettyChannel, nettyRequest);
        boolean compress = ServletUtil.isCompress(getServletConfig());
        final Request request = new HttpRequest(nettyChannel, getServletConfig().getServletContext(), nettyRequest, response, compress);
        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.HTTP_DECORATOR);
        // 长连接推送
        boolean longHttp = RequestUtil.isLongHttp(request.getCommand());
        nettyChannel.attr(AttributeKey.valueOf(RequestUtil.LONG_HTTP)).set(longHttp);
        service(request, response, channel, message);
        if (longHttp) {
            return;
        }
    }

}
