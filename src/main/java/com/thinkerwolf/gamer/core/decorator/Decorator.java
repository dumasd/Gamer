package com.thinkerwolf.gamer.core.decorator;

import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public interface Decorator {

    Object decorate(Model<?> model, Request request, Response response);

}
