package com.thinkerwolf.gamer.core.mvc.view;


import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.remoting.Content;
import com.thinkerwolf.gamer.remoting.Protocol;

public class ByteView extends AbstractView{
    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) throws Exception {
        Protocol protocol = request.getProtocol();
        Decorator decorator = ServiceLoader.getService(request.getAttribute(Request.DECORATOR_ATTRIBUTE).toString(), Decorator.class);
        if (Protocol.TCP.equals(protocol)) {
            // wrapper
            response.setContentType(Content.CONTENT_BYTES);
            response.write(decorator.decorate(model, request, response));
        } else if (Protocol.HTTP.equals(protocol)) {
            response.setContentType("application/octet-stream");
            response.write(decorator.decorate(model, request, response));
        } else if (Protocol.WEBSOCKET.equals(protocol)) {
            response.setContentType(Content.CONTENT_BYTES);
            response.write(decorator.decorate(model, request, response));
        }
    }
}
