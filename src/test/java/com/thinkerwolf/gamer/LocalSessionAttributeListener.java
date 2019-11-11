package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.servlet.SessionAttributeEvent;
import com.thinkerwolf.gamer.core.servlet.SessionAttributeListener;

public class LocalSessionAttributeListener implements SessionAttributeListener {
    @Override
    public void attributeAdded(SessionAttributeEvent sae) {
        System.out.println("session attributeAdded : " + sae.getSource());
    }

    @Override
    public void attributeRemoved(SessionAttributeEvent sae) {

    }
}
