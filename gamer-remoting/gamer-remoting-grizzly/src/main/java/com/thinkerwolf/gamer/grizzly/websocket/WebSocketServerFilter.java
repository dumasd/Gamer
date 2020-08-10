package com.thinkerwolf.gamer.grizzly.websocket;

import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.HttpResponsePacket;
import org.glassfish.grizzly.websockets.HandshakeException;
import org.glassfish.grizzly.websockets.WebSocketEngine;

import java.io.IOException;

public class WebSocketServerFilter extends AbstractWebSocketFilter {


    // ------------------------------------------------------------ Constructors


    public WebSocketServerFilter() {
        super();
    }

    public WebSocketServerFilter(long wsTimeoutInSeconds) {
        super(wsTimeoutInSeconds);
    }


    // ---------------------------------------- Methods from BaseWebSocketFilter

    @Override
    protected NextAction handleHandshake(FilterChainContext ctx, HttpContent content) throws IOException {
        return handleServerHandshake(ctx, content);
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Handle server-side websocket handshake
     *
     * @param ctx            {@link FilterChainContext}
     * @param requestContent HTTP message
     * @throws {@link IOException}
     */
    private NextAction handleServerHandshake(final FilterChainContext ctx,
                                             final HttpContent requestContent)
            throws IOException {

        // get HTTP request headers
        final HttpRequestPacket request = (HttpRequestPacket) requestContent.getHttpHeader();
        try {
            if (doServerUpgrade(ctx, requestContent)) {
                return ctx.getInvokeAction(); // not a WS request, pass to the next filter.
            }
            setIdleTimeout(ctx);
        } catch (HandshakeException e) {
            ctx.write(composeHandshakeError(request, e));
            throw e;
        }
        requestContent.recycle();

        return ctx.getStopAction();

    }

    protected boolean doServerUpgrade(final FilterChainContext ctx,
                                      final HttpContent requestContent) throws IOException {
        return !WebSocketEngine.getEngine().upgrade(ctx, requestContent);
    }


    private static HttpResponsePacket composeHandshakeError(final HttpRequestPacket request,
                                                            final HandshakeException e) {
        final HttpResponsePacket response = request.getResponse();
        response.setStatus(e.getCode());
        response.setReasonPhrase(e.getMessage());
        return response;
    }
}
