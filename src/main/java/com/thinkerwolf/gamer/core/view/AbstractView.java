package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public abstract class AbstractView implements View {

    @Override
    public void render(Model model, Request request, Response response) throws Exception {
        prepareRender(model, request, response);
        doRender(model, request, response);
    }

    protected abstract void prepareRender(Model model, Request request, Response response);

    protected abstract void doRender(Model model, Request request, Response response) throws Exception;


}
