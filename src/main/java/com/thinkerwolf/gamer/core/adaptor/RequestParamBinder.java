package com.thinkerwolf.gamer.core.adaptor;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

public class RequestParamBinder implements ParamBinder {
    @Override
    public Object inject(Request request, Response response) throws Exception {
        return request;
    }
}
