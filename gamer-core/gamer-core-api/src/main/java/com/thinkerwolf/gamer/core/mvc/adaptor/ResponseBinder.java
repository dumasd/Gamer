package com.thinkerwolf.gamer.core.mvc.adaptor;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class ResponseBinder implements ParamBinder {
    @Override
    public Object inject(Request request, Response response) throws Exception {
        return response;
    }
}
