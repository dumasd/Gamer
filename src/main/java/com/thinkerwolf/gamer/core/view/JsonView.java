package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.ByteModel;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Protocol;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

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
        if (model instanceof ByteModel) {
            ((ByteModel) model).getData();
        }
        Protocol protocol = request.getProtocol();
        switch (protocol) {
            case TCP:
                // wrapper

                response.setContentType("json");

                break;
            case HTTP:
                // wrapper json

                break;
            case WEBSOCKET:

                break;
        }

    }


}
