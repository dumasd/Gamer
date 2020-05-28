package com.thinkerwolf.gamer.swagger.action;

import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.mvc.model.JsonModel;
import com.thinkerwolf.gamer.core.mvc.view.JsonView;
import com.thinkerwolf.gamer.swagger.Docket;
import com.thinkerwolf.gamer.swagger.UiConfiguration;
import com.thinkerwolf.gamer.swagger.dto.SwaggerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Action(
        views = {
                @View(name = "json", type = JsonView.class),
        }
)
public class ApiResourcesAction {

    @Value("${gamer.documentation.swagger.v1.path:/api-docs}")
    private String swagger1Url;

    @Value("${gamer.documentation.swagger.v2.path:/v2/api-docs}")
    private String swagger2Url;


    @Autowired(required = false)
    UiConfiguration uiConfiguration;

    @Command("swagger-resources/configuration/ui")
    public JsonModel uiConfiguration() {
        return new JsonModel(Optional.ofNullable(uiConfiguration).orElse(UiConfiguration.DEFAULT));
    }

    @Command("swagger-resources")
    public JsonModel swaggerResources() {
        List<SwaggerResource> resources = new ArrayList<>();
        SwaggerResource sr = resource(swagger2Url, Docket.DEFAULT_GROUP_NAME);
        sr.setSwaggerVersion("2.0");
        resources.add(sr);
        return new JsonModel(resources);
    }

    private SwaggerResource resource(String baseUrl, String groupName) {
        SwaggerResource resource = new SwaggerResource();
        resource.setName(groupName);
        if (Docket.DEFAULT_GROUP_NAME.equals(groupName)) {
            resource.setLocation(baseUrl);
            return resource;
        }
        resource.setLocation(baseUrl + "?group=");
        return resource;
    }

}
