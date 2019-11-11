package com.thinkerwolf.gamer.core.mvc.adaptor;

import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

/**
 * 参数转化器
 *
 * @author wukai
 */
public interface ParamAdaptor {

    Object[] convert(Request request, Response response) throws Exception;

}
