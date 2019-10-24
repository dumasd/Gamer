package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;

/**
 * @author wukai
 */
public interface View {
    /**
     * 用模型数据渲染视图
     *
     * @param model    模型数据
     * @param request  请求
     * @param response 响应
     */
    void render(Model model, Request request, Response response);

}
