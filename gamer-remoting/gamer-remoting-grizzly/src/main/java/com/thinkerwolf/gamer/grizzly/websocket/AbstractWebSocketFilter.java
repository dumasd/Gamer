package com.thinkerwolf.gamer.grizzly.websocket;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpHeader;
import org.glassfish.grizzly.memory.Buffers;
import org.glassfish.grizzly.websockets.*;

import java.io.IOException;


public abstract class AbstractWebSocketFilter extends BaseWebSocketFilter {
    public AbstractWebSocketFilter() {
    }

    public AbstractWebSocketFilter(long wsTimeoutInSeconds) {
        super(wsTimeoutInSeconds);
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        // Get the Grizzly Connection
        final Connection connection = ctx.getConnection();
        // Get the parsed HttpContent (we assume prev. filter was HTTP)
        final HttpContent message = ctx.getMessage();
        // Get the HTTP header
        final HttpHeader header = message.getHttpHeader();
        // Try to obtain associated WebSocket
        final WebSocketHolder holder = WebSocketHolder.get(connection);

        WebSocket ws = WebSocketHolder.getWebSocket(connection);
        if (ws == null || !ws.isConnected()) {
            if (!webSocketInProgress(connection) &&
                    !"websocket".equalsIgnoreCase(header.getUpgrade())) {
                return ctx.getInvokeAction();
            }

            try {
                // Handle handshake
                return handleHandshake(ctx, message);
            } catch (HandshakeException e) {
                onHandshakeFailure(connection, e);
            }

            // Handshake error
            return ctx.getStopAction();
        }
        // this is websocket with the completed handshake
        if (message.getContent().hasRemaining()) {
            // get the frame(s) content

            Buffer buffer = message.getContent();
            message.recycle();
            // check if we're currently parsing a frame
            try {
                while (buffer != null && buffer.hasRemaining()) {
                    if (holder.buffer != null) {
                        buffer = Buffers.appendBuffers(
                                ctx.getMemoryManager(), holder.buffer, buffer);

                        holder.buffer = null;
                    }
                    final DataFrame result = holder.handler.unframe(buffer);
                    if (result == null) {
                        holder.buffer = buffer;
                        break;
                    } else {
                        if (result.isLast()) {
                            ctx.setMessage(result);
                            return ctx.getInvokeAction();
                        }
                    }
                }
            } catch (FramingException e) {
                holder.webSocket.onClose(new ClosingFrame(e.getClosingCode(), e.getMessage()));
            } catch (Exception wse) {
                holder.webSocket.onClose(new ClosingFrame(1011, wse.getMessage()));
            }
        }
        return ctx.getStopAction();
    }
}
