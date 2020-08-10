package com.thinkerwolf.gamer.grizzly.websocket;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.websockets.*;

public class DefaultApplication extends WebSocketApplication {

    private static final Logger logger = InternalLoggerFactory.getLogger(DefaultApplication.class);
    private URL url;
    private ChannelHandler handler;

    public DefaultApplication(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    public DefaultApplication() {
    }

    protected static InternalWebsocket webSocket(WebSocket socket) {
        return (InternalWebsocket) socket;
    }

    @Override
    public WebSocket createSocket(ProtocolHandler handler, HttpRequestPacket requestPacket, WebSocketListener... listeners) {
        return new InternalWebsocket(handler, listeners);
    }

    @Override
    public void onConnect(WebSocket socket) {
        logger.info("Connected: {}", socket);
        super.onConnect(socket);
    }

    @Override
    public void onPing(WebSocket socket, byte[] bytes) {
        logger.debug("Ping received: {}", bytes.length);
        socket.sendPong(bytes);
    }

    @Override
    public void onPong(WebSocket socket, byte[] bytes) {
        logger.debug("Pong received: {}", bytes.length);
    }

    @Override
    public void onMessage(WebSocket socket, String text) {

    }

    @Override
    public void onMessage(WebSocket socket, byte[] bytes) {
        logger.info("Byte message received: length={}", bytes.length);
    }

    @Override
    public void onFragment(WebSocket socket, String fragment, boolean last) {
        logger.info("String fragment received: [{}], last={}", fragment, last);

    }

    @Override
    public void onFragment(WebSocket socket, byte[] fragment, boolean last) {
        logger.info("String fragment received: {}, last={}", fragment.length, last);
    }

}
