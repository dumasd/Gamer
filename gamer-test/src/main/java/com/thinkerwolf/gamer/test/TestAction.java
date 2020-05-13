package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.model.FreemarkerModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.mvc.view.HtmlView;
import com.thinkerwolf.gamer.core.mvc.view.JsonView;
import com.thinkerwolf.gamer.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Action(views = {
        @View(name = "byte", type = JsonView.class),
        @View(name = "freemarker", type = HtmlView.class)
})
public class TestAction {

    @Autowired
    private ITestService testService;

    @Command("test@jjj*")
    public ByteModel getTest(Request request, @RequestParam("num") int num) {
        return new ByteModel(testService.serverInfo(num));
    }

    @Command("test@hello")
    public ByteModel getTest2(Request request, @RequestParam("name") String name) {
        return new ByteModel(testService.sayHello(name));
    }

    @Command("index")
    public FreemarkerModel index() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "Freemarker");
        return new FreemarkerModel("index.ftl", data);
    }

    @Command("user@login")
    public ByteModel login(Request request, @RequestParam("username") String username, @RequestParam("password") String password) {
        request.getSession(true);
        return new ByteModel("{}".getBytes());
    }

}
