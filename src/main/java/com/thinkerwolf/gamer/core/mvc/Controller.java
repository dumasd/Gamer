package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.util.regex.Pattern;

public interface Controller {

    String getCommand();

    Pattern getMatcher();

    void handle(Request request, Response response) throws Exception;

}
