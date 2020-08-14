package com.thinkerwolf.gamer.core.grizzly.websocket;

import com.thinkerwolf.gamer.core.mvc.decorator.Decorator;
import com.thinkerwolf.gamer.core.mvc.model.Model;
import com.thinkerwolf.gamer.core.servlet.Request;
import com.thinkerwolf.gamer.core.servlet.Response;
import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.frametypes.BinaryFrameType;
import org.glassfish.grizzly.websockets.frametypes.TextFrameType;


import java.util.HashSet;
import java.util.Set;

import static com.thinkerwolf.gamer.remoting.Content.*;

public class WebsocketDecorator implements Decorator {

    private static final Set<Object> SUPPORTED_CONTENTS = new HashSet<>();

    static {
        SUPPORTED_CONTENTS.add(CONTENT_BYTES);
        SUPPORTED_CONTENTS.add(CONTENT_TEXT);
        SUPPORTED_CONTENTS.add(CONTENT_JSON);
        SUPPORTED_CONTENTS.add(CONTENT_EXCEPTION);
    }

    @Override
    public Object decorate(Model<?> model, Request request, Response response) {
        checkContent(response.getContentType());
        byte[] content = model.getBytes();
        final int opcode = (int) response.getContentType();
        DataFrame frame;
        if (opcode == CONTENT_BYTES) {
            frame = new BinaryFrameType().create(true, content);
        } else {
            frame = new TextFrameType().create(true, content);
        }
        return frame;
    }

    private static void checkContent(Object contentType) {
        if (!SUPPORTED_CONTENTS.contains(contentType)) {
            throw new UnsupportedOperationException("Unsupported websocket content type " + contentType);
        }
    }

}
