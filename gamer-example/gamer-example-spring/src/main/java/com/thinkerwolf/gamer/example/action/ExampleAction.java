package com.thinkerwolf.gamer.example.action;

import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.mvc.model.FreemarkerModel;
import com.thinkerwolf.gamer.core.mvc.model.JsonModel;
import com.thinkerwolf.gamer.core.mvc.view.HtmlView;
import com.thinkerwolf.gamer.core.mvc.view.JsonView;
import com.thinkerwolf.gamer.example.service.IExampleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Action(views = {
        @View(name = FreemarkerModel.NAME, type = HtmlView.class),
        @View(name = JsonModel.NAME, type = JsonView.class),
})
public class ExampleAction {

    @Autowired
    IExampleService exampleService;

    @Command("hello/api")
    public JsonModel hello(@RequestParam("name") String name) {
        return new JsonModel(exampleService.helloApi(name));
    }

    @Command("hello/index")
    public FreemarkerModel helloIndex(@RequestParam("name") String name) {
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "Hello " + name);
        return new FreemarkerModel("index.ftl", data);
    }


}
