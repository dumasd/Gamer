package com.thinkerwolf.gamer.swagger.action;

import com.thinkerwolf.gamer.core.annotation.*;
import com.thinkerwolf.gamer.core.mvc.model.JsonModel;
import com.thinkerwolf.gamer.core.mvc.view.JsonView;
import com.thinkerwolf.gamer.swagger.Docket;
import com.thinkerwolf.gamer.swagger.Document;
import com.thinkerwolf.gamer.swagger.mapper.DtoToSwagger2Mapper;
import com.thinkerwolf.gamer.swagger.SwaggerContext;
import io.swagger.models.Swagger;
import org.apache.commons.lang.StringUtils;


@Action(
        views = {
                @View(name = "json", type = JsonView.class),
        }
)
public class Swagger2Action {

    @Command(value = "v2/api-docs")
    public JsonModel getDocument(@RequestParam("group") String group) {
        if (StringUtils.isBlank(group)) {
            group = Docket.DEFAULT_GROUP_NAME;
        }
        Document document = SwaggerContext.getDocument(group);
        Swagger swagger = new DtoToSwagger2Mapper().mapDocument(document);
        return new JsonModel(swagger);
    }
}
