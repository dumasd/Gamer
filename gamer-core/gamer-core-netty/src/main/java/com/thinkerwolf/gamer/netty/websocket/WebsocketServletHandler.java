package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.netty.AbstractServletHandler;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.MapUtils;

import java.nio.ByteBuffer;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

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
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object sent(Channel channel, Object message) throws RemotingException {
        if (message instanceof ChannelBuffer) {
            ChannelBuffer cb = (ChannelBuffer) message;
            int opcode = cb.readInt();
            cb.readInt();
            int cmdLen = cb.readInt();
            int contentLen = cb.readInt();
            cb.skipBytes(cmdLen);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(contentLen);
            cb.readBytes(byteBuffer);
            byteBuffer.flip();
            ByteBuf nettyBuf = Unpooled.wrappedBuffer(byteBuffer);

            if (opcode == ResponseUtil.CONTENT_TEXT
                    || opcode == ResponseUtil.CONTENT_JSON
                    || opcode == ResponseUtil.CONTENT_EXCEPTION) {
                return new TextWebSocketFrame(nettyBuf);
            } else {
                return new BinaryWebSocketFrame(nettyBuf);
            }
        }
        return super.sent(channel, message);
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
        final WebsocketRequest request = new WebsocketRequest(requestId, new String(command, CharsetUtil.UTF_8), channel, content, getServletConfig());
        final WebsocketResponse response = new WebsocketResponse(channel);

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
        int requestId = RequestUtil.getRequestId(params);
        WebsocketRequest request = new WebsocketRequest(requestId, command, channel, text.getBytes(UTF_8), getServletConfig());
        WebsocketResponse response = new WebsocketResponse(channel);

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);
        service(request, response, channel, frame);
    }
}
