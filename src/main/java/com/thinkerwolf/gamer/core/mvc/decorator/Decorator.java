package com.thinkerwolf.gamer.core.mvc.decorator;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

@SPI
public interface Decorator {

    Object decorate(Model<?> model, Request request, Response response);

}
