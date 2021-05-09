package com.thinkerwolf.gamer.core.grizzly;

import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import com.thinkerwolf.gamer.core.grizzly.http.HttpRequest;
import com.thinkerwolf.gamer.core.grizzly.http.HttpResponse;
import com.thinkerwolf.gamer.core.grizzly.http.InternalHttpUtil;
import com.thinkerwolf.gamer.core.grizzly.tcp.TcpRequest;
import com.thinkerwolf.gamer.core.grizzly.tcp.TcpResponse;
import com.thinkerwolf.gamer.core.grizzly.websocket.WebsocketRequest;
import com.thinkerwolf.gamer.core.grizzly.websocket.WebsocketResponse;
import com.thinkerwolf.gamer.core.servlet.AbstractServletHandler;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;
import org.apache.commons.collections.MapUtils;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.frametypes.BinaryFrameType;
import org.glassfish.grizzly.websockets.frametypes.PingFrameType;
import org.glassfish.grizzly.websockets.frametypes.PongFrameType;
import org.glassfish.grizzly.websockets.frametypes.TextFrameType;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GrizzlyServletHandler extends AbstractServletHandler {

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof Packet) {
            processTcp(channel, (Packet) message);
        } else if (message instanceof HttpContent) {
            processHttpContent(channel, (HttpContent) message);
        } else if (message instanceof DataFrame) {
            processDataFrame(channel, (DataFrame) message);
        } else {
            processOtherMessage(channel, message);
        }
    }

    protected void processTcp(Channel channel, Packet packet) throws RemotingException {
        TcpRequest request = new TcpRequest(packet.getRequestId(), packet.getCommand(), channel, packet.getContent(), getServletConfig());
        request.setAttribute(Request.DECORATOR_ATTRIBUTE, GrizzlyConstants.TCP_DECORATOR);
        TcpResponse response = new TcpResponse(channel);
        service(request, response, channel, packet);
    }

    protected void processHttpContent(Channel channel, HttpContent httpContent) throws RemotingException {
        HttpRequest request = InternalHttpUtil.createRequest(httpContent, channel, getServletConfig());
        HttpResponse response = new HttpResponse(channel);
        request.setAttribute(Request.DECORATOR_ATTRIBUTE, GrizzlyConstants.HTTP_DECORATOR);
        service(request, response, channel, httpContent);
    }

    private void processDataFrame(Channel channel, DataFrame frame) throws RemotingException {
        if (frame.getType() instanceof TextFrameType) {
            String text = frame.getTextPayload();
            Map<String, Object> params = RequestUtil.parseParams(text);
            String command = MapUtils.getString(params, "command");
            if (command == null) {
                channel.close();
                return;
            }
            int requestId = RequestUtil.getRequestId(params);
            WebsocketRequest request = new WebsocketRequest(requestId, command, channel, text.getBytes(UTF_8), getServletConfig());
            WebsocketResponse response = new WebsocketResponse(channel);

            request.setAttribute(Request.DECORATOR_ATTRIBUTE, GrizzlyConstants.WEBSOCKET_DECORATOR);
            service(request, response, channel, frame);
        } else if (frame.getType() instanceof BinaryFrameType) {
            ChannelBuffer buf = ChannelBuffers.wrappedBuffer(frame.getBytes());
            buf.readInt();
            int requestId = buf.readInt();
            int commandLen = buf.readInt();
            int contentLen = buf.readInt();

            byte[] command = new byte[commandLen];
            byte[] content = new byte[contentLen];

            buf.readBytes(command);
            buf.readBytes(content);
            final WebsocketRequest request = new WebsocketRequest(requestId, new String(command, UTF_8), channel, content, getServletConfig());
            final WebsocketResponse response = new WebsocketResponse(channel);
            request.setAttribute(Request.DECORATOR_ATTRIBUTE, GrizzlyConstants.WEBSOCKET_DECORATOR);
            service(request, response, channel, frame);

        } else if (frame.getType() instanceof PingFrameType) {
            channel.send(new PongFrameType().create(true, frame.getBytes()));
        }
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
