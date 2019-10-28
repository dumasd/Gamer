package com.thinkerwolf.gamer.core.view;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.core.decorator.Decorator;
import com.thinkerwolf.gamer.core.model.Model;
import com.thinkerwolf.gamer.core.model.ResourceModel;
import com.thinkerwolf.gamer.core.servlet.*;

import java.util.HashMap;
import java.util.Map;

public class ResourceView extends AbstractView {

    private Map<Protocol, Map<String, String>> mimeMappings;

    public ResourceView() {
        mimeMappings = new HashMap<>();

        Map<String, String> tcpMimeMappings = new HashMap<>();
        tcpMimeMappings.put("json", "json");
        tcpMimeMappings.put("txt", "text/plain");
        tcpMimeMappings.put("png", "image/png");
        tcpMimeMappings.put("gif", "image/gif");
        tcpMimeMappings.put("jpg", "image/jpeg");
        tcpMimeMappings.put("jpeg", "image/jpeg");
        mimeMappings.put(Protocol.TCP, tcpMimeMappings);


        Map<String, String> httpMimeMappings = new HashMap<>();
        httpMimeMappings.put("html", "text/html");
        httpMimeMappings.put("txt", "text/plain");
        httpMimeMappings.put("css", "text/css");
        httpMimeMappings.put("json", "application/json");
        httpMimeMappings.put("js", "application/x-javascript");
        httpMimeMappings.put("png", "image/png");
        httpMimeMappings.put("gif", "image/gif");
        httpMimeMappings.put("jpg", "image/jpeg");
        httpMimeMappings.put("jpeg", "image/jpeg");
        httpMimeMappings.put("ico", "image/x-icon");

        mimeMappings.put(Protocol.HTTP, httpMimeMappings);

    }

    @Override
    protected void prepareRender(Model model, Request request, Response response) {

    }

    @Override
    protected void doRender(Model model, Request request, Response response) throws Exception {
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
        Map<String, String> mimes = mimeMappings.get(protocol);
        if (mimes == null) {
            return null;
        }
        return mimes.get(extension);
    }

}
