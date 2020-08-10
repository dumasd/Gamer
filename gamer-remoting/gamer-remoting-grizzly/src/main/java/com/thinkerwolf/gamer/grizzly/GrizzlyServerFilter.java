package com.thinkerwolf.gamer.grizzly;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.remoting.ChannelHandler;
import com.thinkerwolf.gamer.remoting.RemotingException;
import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.FilterChainEvent;
import org.glassfish.grizzly.filterchain.NextAction;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;

public class GrizzlyServerFilter extends BaseFilter {

    private URL url;
    private ChannelHandler handler;



    public GrizzlyServerFilter(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public NextAction handleAccept(FilterChainContext ctx) throws IOException {
        try {
            GrizzlyChannel ch = GrizzlyChannel.getOrAddChannel(ctx.getConnection(), url, handler);
            handler.registered(ch);
            return super.handleAccept(ctx);
        } catch (RemotingException e) {
            throw new IOException(e);
        }
    }

    @Override
    public NextAction handleRead(FilterChainContext ctx) throws IOException {
        try {
            GrizzlyChannel ch = GrizzlyChannel.getOrAddChannel(ctx.getConnection(), url, handler);
            handler.received(ch, ctx.getMessage());
            return ctx.getStopAction();
        } catch (RemotingException e) {
            throw new IOException(e);
        } finally {
            GrizzlyChannel.removeChannelIfDisconnected(ctx.getConnection());
        }
    }

    @Override
    public NextAction handleWrite(FilterChainContext ctx) throws IOException {
        try {
            GrizzlyChannel ch = GrizzlyChannel.getOrAddChannel(ctx.getConnection(), url, handler);
            Object msg = handler.sent(ch, ctx.getMessage());
            ctx.setMessage(msg);
            return super.handleWrite(ctx);
        } catch (RemotingException e) {
            throw new IOException(e);
        } finally {
            GrizzlyChannel.removeChannelIfDisconnected(ctx.getConnection());
        }
    }

    @Override
    public NextAction handleClose(FilterChainContext ctx) throws IOException {
        try {
            GrizzlyChannel ch = GrizzlyChannel.getOrAddChannel(ctx.getConnection(), url, handler);
            if (ch != null) {
                handler.disconnected(ch);
            }
        } catch (RemotingException e) {
            throw new IOException(e);
        } finally {
            GrizzlyChannel.removeChannelIfDisconnected(ctx.getConnection());
        }
        return super.handleClose(ctx);
    }

    @Override
    public NextAction handleConnect(FilterChainContext ctx) throws IOException {
        try {
            GrizzlyChannel ch = GrizzlyChannel.getOrAddChannel(ctx.getConnection(), url, handler);
            handler.connected(ch);
            return super.handleConnect(ctx);
        } catch (RemotingException e) {
            throw new IOException(e);
        }
    }

    @Override
    public NextAction handleEvent(FilterChainContext ctx, FilterChainEvent event) throws IOException {
        try {
            GrizzlyChannel ch = GrizzlyChannel.getOrAddChannel(ctx.getConnection(), url, handler);
            handler.event(ch, event);
            return super.handleEvent(ctx, event);
        } catch (RemotingException e) {
            throw new IOException(e);
        } finally {
            GrizzlyChannel.removeChannelIfDisconnected(ctx.getConnection());
        }
    }
}
