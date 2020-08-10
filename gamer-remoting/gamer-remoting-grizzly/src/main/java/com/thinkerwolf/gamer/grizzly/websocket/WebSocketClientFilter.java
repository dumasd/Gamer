package com.thinkerwolf.gamer.grizzly.websocket;

import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpResponsePacket;
import org.glassfish.grizzly.websockets.WebSocketHolder;

import java.io.IOException;

public class WebSocketClientFilter extends AbstractWebSocketFilter {


    // ----------------------------------------------------- Methods from Filter

    private static NextAction handleClientHandShake(FilterChainContext ctx, HttpContent content) {
        final WebSocketHolder holder = WebSocketHolder.get(ctx.getConnection());
        holder.handshake.validateServerResponse((HttpResponsePacket) content.getHttpHeader());
        holder.webSocket.onConnect();

        if (content.getContent().hasRemaining()) {
            return ctx.getRerunFilterAction();
        } else {
            content.recycle();
            return ctx.getStopAction();
        }
    }

    // ---------------------------------------- Methods from BaseWebSocketFilter

    @Override
    public NextAction handleConnect(FilterChainContext ctx) throws IOException {
        // Get connection
        final Connection connection = ctx.getConnection();
        // check if it's websocket connection
        if (!webSocketInProgress(connection)) {
            // if not - pass processing to a next filter
            return ctx.getInvokeAction();
        }

        WebSocketHolder.get(connection).handshake.initiate(ctx);
        // call the next filter in the chain
        return ctx.getInvokeAction();
    }


    // --------------------------------------------------------- Private Methods

    @Override
    protected NextAction handleHandshake(FilterChainContext ctx, HttpContent content) throws IOException {
        return handleClientHandShake(ctx, content);
    }
}
