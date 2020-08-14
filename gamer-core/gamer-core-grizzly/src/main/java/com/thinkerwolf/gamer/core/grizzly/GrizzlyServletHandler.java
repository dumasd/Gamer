package com.thinkerwolf.gamer.core.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
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
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.frametypes.BinaryFrameType;
import org.glassfish.grizzly.websockets.frametypes.PingFrameType;
import org.glassfish.grizzly.websockets.frametypes.PongFrameType;
import org.glassfish.grizzly.websockets.frametypes.TextFrameType;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GrizzlyServletHandler extends AbstractServletHandler {
    public GrizzlyServletHandler(URL url) {
        super(url);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        if (message instanceof Packet) {
            Packet packet = (Packet) message;
            TcpRequest request = new TcpRequest(packet.getRequestId(), packet.getCommand(), channel, packet.getContent(), getServletConfig());
            request.setAttribute(Request.DECORATOR_ATTRIBUTE, GrizzlyConstants.TCP_DECORATOR);
            TcpResponse response = new TcpResponse(channel);
            service(request, response, channel, message);
        } else if (message instanceof HttpContent) {
            processHttpContent(channel, (HttpContent) message);
        } else if (message instanceof DataFrame) {
            processDataFrame(channel, (DataFrame) message);
        }
    }


    private void processHttpContent(Channel channel, HttpContent httpContent) throws RemotingException {
        HttpRequestPacket requestPacket = (HttpRequestPacket) httpContent.getHttpHeader();
        Method httpMethod = requestPacket.getMethod();
        if ("GET".equalsIgnoreCase(httpMethod.getMethodString())) {

        } else if ("PUT".equalsIgnoreCase(httpMethod.getMethodString())) {

        }
        requestPacket.getRequestURI();
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

}
