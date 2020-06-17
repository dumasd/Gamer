package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.util.regex.Pattern;

public interface Invocation {

    String getCommand();

    boolean isMatch(String command);

    void handle(Request request, Response response) throws Exception;

}
