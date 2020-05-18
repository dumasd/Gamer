package com.thinkerwolf.gamer.netty.websocket;

import com.thinkerwolf.gamer.netty.IServerHandler;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.netty.concurrent.ChannelRunnable;
import com.thinkerwolf.gamer.netty.concurrent.CountAwareThreadPoolExecutor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@ChannelHandler.Sharable
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> implements IServerHandler {

    private static final Logger LOG = InternalLoggerFactory.getLogger(WebSocketServerHandler.class);

    private ServletConfig servletConfig;

    private Servlet servlet;

    private Executor executor;

    public WebSocketServerHandler(Executor executor, ServletConfig servletConfig) {
        this.executor = executor;
        this.servletConfig = servletConfig;
        this.servlet = (Servlet) servletConfig.getServletContext().getAttribute(ServletContext.ROOT_SERVLET_ATTRIBUTE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            WebSocketFrame frame = (WebSocketFrame) msg;
            if (frame instanceof CloseWebSocketFrame) {
                ctx.channel().writeAndFlush(frame).addListener(ChannelFutureListener.CLOSE);
            } else if (frame instanceof PingWebSocketFrame) {
                ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
            } else if (frame instanceof BinaryWebSocketFrame) {
                processBinaryFrame((BinaryWebSocketFrame) frame, ctx);
            } else if (frame instanceof TextWebSocketFrame) {
                processTextFrame((TextWebSocketFrame) frame, ctx);
            } else {
                throw new UnsupportedOperationException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }


    private void processBinaryFrame(BinaryWebSocketFrame frame, final ChannelHandlerContext ctx) {
        ByteBuf buf = frame.content();

        buf.readInt();
        int requestId = buf.readInt();
        int commandLen = buf.readInt();
        int contentLen = buf.readInt();

        byte[] command = new byte[commandLen];
        byte[] content = new byte[contentLen];

        buf.readBytes(command);
        buf.readBytes(content);
        final WebSocketRequest request = new WebSocketRequest(requestId, new String(command, CharsetUtil.UTF_8), ctx, content, servletConfig.getServletContext());
        final WebSocketResponse response = new WebSocketResponse(ctx.channel());

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);

        if (executor != null) {
            executor.execute(new ChannelRunnable(ctx.channel()) {
                @Override
                public void run() {
                    service(servlet, request, response, ctx);
                }
            });
        } else {
            service(servlet, request, response, ctx);
        }
    }


    private static void service(Servlet servlet, WebSocketRequest request, WebSocketResponse response, ChannelHandlerContext ctx) {
        try {
            servlet.service(request, response);
        } catch (Exception e) {
            // 捕捉到非业务层异常，异常很严重
            LOG.error("Serious error happen", e);
            ctx.writeAndFlush(new CloseWebSocketFrame());
        }
    }

    private void processTextFrame(TextWebSocketFrame frame, ChannelHandlerContext ctx) {
        String text = frame.text();
        // command=3333&requestId=1&
        Map<String, Object> params = RequestUtil.parseParams(text);

        String command = MapUtils.getString(params, "command");
        if (command == null) {
            ctx.writeAndFlush(new TextWebSocketFrame("No command in text message")).addListener(ChannelFutureListener.CLOSE);
            return;
        }
        int requestId = MapUtils.getInteger(params, "requestId", 0);

        WebSocketRequest request = new WebSocketRequest(requestId, command, ctx, text.getBytes(CharsetUtil.UTF_8), servletConfig.getServletContext());
        WebSocketResponse response = new WebSocketResponse(ctx.channel());

        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.WEBSOCKET_DECORATOR);

        if (executor != null) {
            executor.execute(new ChannelRunnable(ctx.channel()) {
                @Override
                public void run() {
                    service(servlet, request, response, ctx);
                }
            });
        } else {
            service(servlet, request, response, ctx);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        LOG.debug("Channel error. channel:" + channel.id()
                + ", isWritable:" + channel.isWritable()
                + ", isOpen:" + channel.isOpen()
                + ", isActive:" + channel.isActive()
                + ", isRegistered:" + channel.isRegistered(), cause);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Channel channel = ctx.channel();
        LOG.debug("Channel Inactive. channel:" + channel.id()
                + ", isOpen:" + channel.isOpen());
        if (executor instanceof CountAwareThreadPoolExecutor) {
            ((CountAwareThreadPoolExecutor) executor).check(ctx.channel());
        }
    }
}
