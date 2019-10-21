package com.thinkerwolf.gamer.core;

import java.util.Map;

public interface Request {

    Object getParameter(String key);

    Map<String, Object> getParameters();

}
