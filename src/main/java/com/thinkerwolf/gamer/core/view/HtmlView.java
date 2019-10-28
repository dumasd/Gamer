package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class HtmlView extends AbstractView {

    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) throws Exception{
        Protocol protocol = request.getProtocol();
        Decorator decorator = ServiceLoader.getService(request.getAttribute(Request.DECORATOR_ATTRIBUTE).toString(), Decorator.class);
        if (protocol == Protocol.TCP) {
            throw new UnsupportedOperationException("Tcp does't support html");
        } else if (protocol == Protocol.HTTP) {
            response.setContentType("text/html");
            response.write(decorator.decorate(model, request, response));
        }
    }
}
