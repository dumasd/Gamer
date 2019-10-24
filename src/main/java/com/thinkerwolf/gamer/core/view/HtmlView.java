package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class HtmlView extends AbstractView {
    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) {
        Protocol protocol = request.getProtocol();
        if (protocol == Protocol.TCP) {
            throw new UnsupportedOperationException("Tcp does't support html");
        }

    }
}
