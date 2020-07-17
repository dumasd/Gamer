package com.thinkerwolf.gamer.netty.tcp;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.Session;
import com.thinkerwolf.gamer.core.servlet.SessionManager;
import com.thinkerwolf.gamer.netty.AbstractServletHandler;
import com.thinkerwolf.gamer.netty.NettyConstants;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.RemotingException;
import com.thinkerwolf.gamer.remoting.tcp.Packet;

/**
 * @author wukai
 * @since 2020-05-05
 */
public class TcpServletHandler extends AbstractServletHandler {

    public TcpServletHandler(URL url) {
        super(url);
    }

    @Override
    public void registered(Channel channel) throws RemotingException {
        SessionManager sessionManager = (SessionManager) getServletConfig().getServletContext().getAttribute(ServletContext.ROOT_SESSION_MANAGER_ATTRIBUTE);
        if (sessionManager != null) {
            Session session = sessionManager.getSession(null, false);
            if (session != null) {
                channel.setAttr(Session.JSESSION, session.getId());
            }
        }
    }

    @Override
    public void received(Channel ch, Object message) throws RemotingException {
        Packet packet = (Packet) message;
        TcpRequest request = new TcpRequest(packet.getRequestId(), packet.getCommand(), ch, packet.getContent(), getServletConfig());
        request.setAttribute(Request.DECORATOR_ATTRIBUTE, NettyConstants.TCP_GAMER_DECORATOR);
        TcpResponse response = new TcpResponse(ch);
        service(request, response, ch, message);
    }

}
