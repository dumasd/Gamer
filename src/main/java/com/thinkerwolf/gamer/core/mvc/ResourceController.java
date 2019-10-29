package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.core.model.ResourceModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.servlet.ResponseStatus;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.core.view.View;

/**
 * 静态资源
 *
 * @author wukai
 */
public class ResourceController implements Controller {

    private ResourceManager resourceManager;

    private View resourceView;

    public ResourceController(ResourceManager resourceManager, View resourceView) {
        this.resourceManager = resourceManager;
        this.resourceView = resourceView;
    }

    @Override
    public String getCommand() {
        return "";
    }

    @Override
    public boolean isMatch(String command) {
        return false;
    }

    @Override
    public void handle(Request request, Response response) throws Exception {
        String command = request.getCommand();
        if (command != null && !command.isEmpty()) {
            ResourceModel resourceModel;
            if (request.getEncoding() != null && request.getEncoding().length() > 0) {
                resourceModel = resourceManager.getResource(command, request.getEncoding());
            } else {
                resourceModel = resourceManager.getResource(command);
            }
            if (resourceModel == null) {
                response.setStatus(ResponseStatus.NOT_FOUND);
                ResponseUtil.renderError("Not found", request, response);
            } else {
                response.setStatus(ResponseStatus.OK);
                resourceView.render(resourceModel, request, response);
            }
        } else {
            // 跳转到默认主页
            response.setStatus(ResponseStatus.NOT_FOUND);
            ResponseUtil.renderError("Not found", request, response);
        }
    }
}
