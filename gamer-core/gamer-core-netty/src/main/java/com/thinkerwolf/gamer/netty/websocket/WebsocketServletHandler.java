package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.AbstractServletHandler;
import com.thinkerwolf.gamer.netty.NettyChannel;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * WebSocket
 *
 * @author wukai
 * @since 2020-06-11
 */
public class WebsocketServletHandler extends AbstractServletHandler {

    public WebsocketServletHandler(URL url) {
        super(url);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) message;
            if (frame instanceof CloseWebSocketFrame) {
                channel.send(frame);
            } else if (frame instanceof PingWebSocketFrame) {
                channel.send(new PongWebSocketFrame(frame.content()));
            } else if (frame instanceof BinaryWebSocketFrame) {
                processBinaryFrame((BinaryWebSocketFrame) frame, channel);
            } else if (frame instanceof TextWebSocketFrame) {
                processTextFrame((TextWebSocketFrame) frame, channel);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }


    private void processBinaryFrame(BinaryWebSocketFrame frame, final Channel channel) {
        ByteBuf buf = frame.content();

        buf.readInt();
        int requestId = buf.readInt();
        int commandLen = buf.readInt();
        int contentLen = buf.readInt();

        byte[] command = new byte[commandLen];
        byte[] content = new byte[contentLen];

        buf.readBytes(command);
        buf.readBytes(content);
        NettyChannel nettyChannel = (NettyChannel) channel;
        final WebsocketRequest request = new WebsocketRequest(requestId, new String(command, CharsetUtil.UTF_8), nettyChannel.innerCh(), content, getServletConfig().getServletContext());
        final WebsocketResponse response = new WebsocketResponse(nettyChannel.innerCh());

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        service(request, response, channel, frame);
    }


    private void processTextFrame(TextWebSocketFrame frame, final Channel channel) throws RemotingException {
        String text = frame.text();
        // command=3333&requestId=1&
        Map<String, Object> params = RequestUtil.parseParams(text);

        String command = MapUtils.getString(params, "command");
        if (command == null) {
            channel.close();
            return;
        }
        NettyChannel nettyChannel = (NettyChannel) channel;
        int requestId = MapUtils.getInteger(params, "requestId", 0);
        WebsocketRequest request = new WebsocketRequest(requestId, command, nettyChannel.innerCh(), text.getBytes(CharsetUtil.UTF_8), getServletConfig().getServletContext());
        WebsocketResponse response = new WebsocketResponse(nettyChannel.innerCh());

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        service(request, response, channel, frame);
    }
}
