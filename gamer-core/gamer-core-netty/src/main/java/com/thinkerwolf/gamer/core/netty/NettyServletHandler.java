package com.thinkerwolf.gamer.core.netty;

import com.thinkerwolf.gamer.core.netty.http.Http2Request;
import com.thinkerwolf.gamer.core.netty.http.Http2Response;
import com.thinkerwolf.gamer.core.netty.http.HttpRequest;
import com.thinkerwolf.gamer.core.netty.http.HttpResponse;
import com.thinkerwolf.gamer.core.netty.tcp.TcpRequest;
import com.thinkerwolf.gamer.core.netty.tcp.TcpResponse;
import com.thinkerwolf.gamer.core.netty.websocket.WebsocketRequest;
import com.thinkerwolf.gamer.core.netty.websocket.WebsocketResponse;
import com.thinkerwolf.gamer.core.servlet.AbstractServletHandler;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.core.util.ServletUtil;
import com.thinkerwolf.gamer.netty.NettyChannel;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.netty.http.Http2HeadersAndDataFrames;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class NettyServletHandler extends AbstractServletHandler {

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof Packet) {
            Packet packet = (Packet) message;
            TcpRequest request =
                    new TcpRequest(
                            packet.getRequestId(),
                            packet.getCommand(),
                            channel,
                            packet.getContent(),
                            getServletConfig());
            request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.TCP_DECORATOR);
            TcpResponse response = new TcpResponse(channel);
            service(request, response, channel, message);
        } else if (message instanceof io.netty.handler.codec.http.HttpRequest) {
            io.netty.handler.codec.http.HttpRequest nettyRequest =
                    (io.netty.handler.codec.http.HttpRequest) message;
            io.netty.channel.Channel nettyChannel = ((NettyChannel) channel).innerCh();
            final Response response = new HttpResponse(channel, nettyRequest);
            boolean compress = ServletUtil.isCompress(getServletConfig());
            final Request request =
                    new HttpRequest(channel, getServletConfig(), nettyRequest, response, compress);
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
        } else if (message instanceof WebSocketFrame) {
            processWebSocketFrame(channel, (WebSocketFrame) message);
        } else {
            processOtherMessage(channel, message);
        }
    }

    protected void processWebSocketFrame(Channel channel, WebSocketFrame frame)
            throws RemotingException {
        if (frame instanceof CloseWebSocketFrame) {
            channel.send(new CloseWebSocketFrame());
        } else if (frame instanceof PingWebSocketFrame) {
            channel.send(new PongWebSocketFrame(frame.content()));
        } else if (frame instanceof BinaryWebSocketFrame) {
            processBinaryFrame((BinaryWebSocketFrame) frame, channel);
        } else if (frame instanceof TextWebSocketFrame) {
            processTextFrame((TextWebSocketFrame) frame, channel);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    protected void processBinaryFrame(BinaryWebSocketFrame frame, final Channel channel) {
        ByteBuf buf = frame.content();

        buf.readInt();
        int requestId = buf.readInt();
        int commandLen = buf.readInt();
        int contentLen = buf.readInt();

        byte[] command = new byte[commandLen];
        byte[] content = new byte[contentLen];

        buf.readBytes(command);
        buf.readBytes(content);
        final WebsocketRequest request =
                new WebsocketRequest(
                        requestId,
                        new String(command, CharsetUtil.UTF_8),
                        channel,
                        content,
                        getServletConfig());
        final WebsocketResponse response = new WebsocketResponse(channel);

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        service(request, response, channel, frame);
    }

    protected void processTextFrame(TextWebSocketFrame frame, final Channel channel) {
        String text = frame.text();
        Map<String, Object> params = RequestUtil.parseParams(text);
        String command = MapUtils.getString(params, "command");
        if (command == null) {
            channel.close();
            return;
        }
        int requestId = RequestUtil.getRequestId(params);
        WebsocketRequest request =
                new WebsocketRequest(
                        requestId, command, channel, text.getBytes(UTF_8), getServletConfig());
        WebsocketResponse response = new WebsocketResponse(channel);

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        service(request, response, channel, frame);
    }

    /**
     * 处理其他类型消息
     *
     * @param channel
     * @param message
     * @throws RemotingException
     */
    protected void processOtherMessage(Channel channel, Object message) throws RemotingException {
        throw new UnsupportedOperationException();
    }
}
