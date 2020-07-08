package com.thinkerwolf.gamer.rpc;

import java.io.Serializable;

public abstract class AbstractConfig implements Serializable {

    /**
     * generated serial uid
     */
    private static final long serialVersionUID = 5496303542068817293L;
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
