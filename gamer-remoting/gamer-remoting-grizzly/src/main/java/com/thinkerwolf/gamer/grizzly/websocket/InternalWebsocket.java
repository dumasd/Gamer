package com.thinkerwolf.gamer.grizzly.websocket;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.websockets.ProtocolHandler;
import org.glassfish.grizzly.websockets.SimpleWebSocket;
import org.glassfish.grizzly.websockets.WebSocketListener;

public class InternalWebsocket extends SimpleWebSocket {

    public InternalWebsocket(ProtocolHandler protocolHandler, WebSocketListener... listeners) {
        super(protocolHandler, listeners);
    }

    public Connection getConnection() {
        return protocolHandler.getConnection();
    }

}
