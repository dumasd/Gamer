package com.thinkerwolf.gamer.netty.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.netty.AbstractServletHandler;
import com.thinkerwolf.gamer.netty.NettyChannel;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.util.InternalHttpUtil;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AttributeKey;

import java.util.List;

public class HttpServletHandler extends AbstractServletHandler {

    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpServletHandler.class);

    public HttpServletHandler(URL url) {
        super(url);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof io.netty.handler.codec.http.HttpRequest) {
            io.netty.handler.codec.http.HttpRequest nettyRequest = (io.netty.handler.codec.http.HttpRequest) message;
            io.netty.channel.Channel nettyChannel = ((NettyChannel) channel).innerCh();
            final Response response = new HttpResponse(nettyChannel, nettyRequest);
            boolean compress = ServletUtil.isCompress(getServletConfig());
            final Request request = new HttpRequest(nettyChannel, getServletConfig().getServletContext(), nettyRequest, response, compress);
            request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.HTTP_DECORATOR);
            // 长连接推送
            boolean longHttp = RequestUtil.isLongHttp(request.getCommand());
            nettyChannel.attr(AttributeKey.valueOf(RequestUtil.LONG_HTTP)).set(longHttp);
            if (longHttp) {
                return;
            }
            service(request, response, channel, message);
        } else if (message instanceof Http2HeadersAndDataFrames) {
            Http2HeadersAndDataFrames frames = (Http2HeadersAndDataFrames) message;
            Http2Response response = new Http2Response(channel);

            Http2Request request = new Http2Request(channel, getServletConfig(), response, frames);
            request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.HTTP_DECORATOR);
            service(request, response, channel, message);
        }
    }


}
