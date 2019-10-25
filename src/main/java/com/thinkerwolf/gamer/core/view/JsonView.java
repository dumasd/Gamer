package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.io.IOException;

/**
 * @author wukai
 */
public class JsonView extends AbstractView {

    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) {
        // json视图
        String decoratorName = request.getAttribute(Request.DECORATOR_ATTRIBUTE).toString();
        Protocol protocol = request.getProtocol();
        Decorator decorator = ServiceLoader.getService(decoratorName, Decorator.class);
        try {
            switch (protocol) {
                case TCP:
                    // wrapper
                    response.setContentType("json");
                    response.write(decorator.decorate(model, request, response));
                    break;
                case HTTP:
                    // wrapper json
                    break;
                case WEBSOCKET:
                    break;
            }
        } catch (IOException e) {

        }


    }


}
