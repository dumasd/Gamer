package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.model.ByteModel;
import com.thinkerwolf.gamer.core.model.FreemarkerModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.view.HtmlView;
import com.thinkerwolf.gamer.core.view.JsonView;

import java.util.HashMap;
import java.util.Map;

@Action(views = {
        @View(name = "byte", type = JsonView.class),
        @View(name = "freemarker", type = HtmlView.class)
})
public class TestAction {

    @Command("test@jjj*")
    public ByteModel getTest(Request request, @RequestParam("num") int num) {
        return new ByteModel(("{\"num\":" + num + ",\"netty\":\"4.1.19\"}").getBytes());
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
