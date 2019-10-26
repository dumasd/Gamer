package com.thinkerwolf.gamer.core.servlet;

import java.util.EventListener;

public interface SessionAttributeListener extends EventListener {

    void attributeAdded(SessionAttributeEvent sae);

    void attributeRemoved(SessionAttributeEvent sae);

}
