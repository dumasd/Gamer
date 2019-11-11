package com.thinkerwolf.gamer.core.mvc.view;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.mvc.model.ResourceModel;
import com.thinkerwolf.gamer.core.servlet.*;
import com.thinkerwolf.gamer.core.util.ResponseUtil;

import java.util.HashMap;
import java.util.Map;

public class ResourceView extends AbstractView {

    private Map<String, String> mimeMappings;

    public ResourceView() {
        mimeMappings = new HashMap<>();

        Map<String, String> httpMimeMappings = mimeMappings;
        httpMimeMappings.put("html", "text/html");
        httpMimeMappings.put("htm", "text/html");
        httpMimeMappings.put("txt", "text/plain");
        httpMimeMappings.put("css", "text/css");

        httpMimeMappings.put("json", "application/json");
        httpMimeMappings.put("js", "application/x-javascript");
        httpMimeMappings.put("woff", "application/x-font-woff");
        httpMimeMappings.put("woff2", "application/font-woff2");
        httpMimeMappings.put("pdf", "application/pdf");

        httpMimeMappings.put("png", "image/png");
        httpMimeMappings.put("gif", "image/gif");
        httpMimeMappings.put("jpg", "image/jpeg");
        httpMimeMappings.put("jpeg", "image/jpeg");
        httpMimeMappings.put("ico", "image/x-icon");
        httpMimeMappings.put("tff", "image/tiff");
        httpMimeMappings.put("svg", "image/svg");
        httpMimeMappings.put("tif", "image/tiff");
    }

    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) throws Exception {

        if (request.getProtocol() == Protocol.TCP) {
            throw new UnsupportedOperationException("Tcp not supported");
        }

        ResourceModel resourceModel = (ResourceModel) model;
        Protocol protocol = request.getProtocol();
        String mime = findMimeType(protocol, resourceModel.getExtension());
        if (mime == null) {
            response.setStatus(ResponseStatus.NOT_IMPLEMENTED);
            ResponseUtil.renderError("Request resource type : " + resourceModel.getExtension() + " is not supported", request, response);
            return;
        }
        response.setContentType(mime);
        response.setStatus(ResponseStatus.OK);
        Decorator decorator = ServiceLoader.getService(request.getAttribute(Request.DECORATOR_ATTRIBUTE).toString(), Decorator.class);
        response.write(decorator.decorate(resourceModel, request, response));
    }


    private String findMimeType(Protocol protocol, String extension) {
        return mimeMappings.get(extension);
    }

}
