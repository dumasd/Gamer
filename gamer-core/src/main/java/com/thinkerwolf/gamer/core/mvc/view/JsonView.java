package com.thinkerwolf.gamer.core.mvc.view;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.ResponseUtil;

/**
 * @author wukai
 */
public class JsonView extends AbstractView {

    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) throws Exception {
        // json视图
        Protocol protocol = request.getProtocol();
        Decorator decorator = ServiceLoader.getService(request.getAttribute(Request.DECORATOR_ATTRIBUTE).toString(), Decorator.class);
        switch (protocol) {
            case TCP:
                // wrapper
                response.setContentType(ResponseUtil.CONTENT_JSON);
                response.write(decorator.decorate(model, request, response));
                break;
            case HTTP:
                response.setContentType("application/json");
                response.write(decorator.decorate(model, request, response));
                break;
            case WEBSOCKET:
                response.setContentType(ResponseUtil.CONTENT_JSON);
                response.write(decorator.decorate(model, request, response));
                break;
        }
    }


}
