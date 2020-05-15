package com.thinkerwolf.gamer.test.action;

import com.thinkerwolf.gamer.core.json.JsonBuilder;
import com.thinkerwolf.gamer.core.json.State;
import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.mvc.model.ByteModel;
import com.thinkerwolf.gamer.core.mvc.model.FreemarkerModel;
import com.thinkerwolf.gamer.core.mvc.model.JsonModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.mvc.view.HtmlView;
import com.thinkerwolf.gamer.core.mvc.view.JsonView;
import com.thinkerwolf.gamer.core.servlet.Session;
import com.thinkerwolf.gamer.core.util.ResponseUtil;
import com.thinkerwolf.gamer.test.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@Action(views = {
        @View(name = "byte", type = JsonView.class),
        @View(name = "freemarker", type = HtmlView.class),
        @View(name = "json", type = JsonView.class),
})
public class TestAction {

    @Autowired
    private ITestService testService;

    @Command("test@jjj*")
    @View(name = "byte", type = JsonView.class)
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

    @Command("user@getUser")
    public JsonModel getUser(Request request, @RequestParam("userId") int userId) {
        request.getSession(true);
        return new JsonModel(JsonBuilder.getSucJson(request, testService.getUser(userId)));
    }

    @Command("user@getFail")
    public JsonModel getUserFail(Request request, @RequestParam("userId") int userId) {
        return new JsonModel(JsonBuilder.getFailJson(request, "No user " + userId));
    }

    @Command("test@push")
    public JsonModel getPush(Request request, @RequestParam("msg") String msg) {
        Session session = request.getSession(true);

        JsonModel j = new JsonModel(JsonBuilder.getPushJson("push@test", testService.getUser(9)));
        session.push(ResponseUtil.CONTENT_JSON, "push@test", j.getBytes());

        return new JsonModel(JsonBuilder.getSucJson(request, testService.tpush(msg)));
    }

}
