package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.exception.MvcException;
import com.thinkerwolf.gamer.core.mvc.model.ResourceModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.core.mvc.view.View;
import org.apache.commons.lang.StringUtils;

/**
 * 静态资源
 *
 * @author wukai
 */
public class ResourceInvocation implements Invocation {

    private final ResourceManager resourceManager;

    private final View resourceView;

    private static final MvcException PATH_IS_BLANK = new MvcException();
    static {
        PATH_IS_BLANK.setStackTrace(new StackTraceElement[]{new StackTraceElement(ResourceInvocation.class.getName(), "handle(request, response)", "", 60)});
    }

    public ResourceInvocation(ResourceManager resourceManager, View resourceView) {
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
        String command = URL.decode(request.getCommand());
        if (StringUtils.isNotBlank(command)) {
            ResourceModel resourceModel;
            if (request.getEncoding() != null && request.getEncoding().length() > 0) {
                resourceModel = resourceManager.getResource(command, request.getEncoding());
            } else {
                resourceModel = resourceManager.getResource(command);
            }
            if (resourceModel == null) {
                throw new MvcException("Resource [" + command + "] not found");
            }
            ResponseUtil.render(resourceView, resourceModel, request, response);
        } else {
            throw PATH_IS_BLANK;
        }
    }
}
