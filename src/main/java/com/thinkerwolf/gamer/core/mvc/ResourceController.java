package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

import java.util.regex.Pattern;

/**
 * 静态资源
 *
 * @author wukai
 */
public class ResourceController implements Controller {




    @Override
    public String getCommand() {
        return null;
    }

    @Override
    public Pattern getMatcher() {
        return null;
    }

    @Override
    public void handle(Request request, Response response) throws Exception {

    }
}
