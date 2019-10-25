package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.annotation.Action;
import com.thinkerwolf.gamer.core.annotation.Command;
import com.thinkerwolf.gamer.core.annotation.RequestParam;
import com.thinkerwolf.gamer.core.annotation.View;
import com.thinkerwolf.gamer.core.model.ByteModel;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.view.JsonView;

@Action(views = {
        @View(name = "byte", type = JsonView.class)
})
public class TestAction {

    @Command("test@jjj")
    public ByteModel getTest(Request request, @RequestParam("num") int num) {
        return new ByteModel(new byte[]{1, 3, 4});
    }

}
