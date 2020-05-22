package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;

import java.util.EventObject;

public class DataEvent extends EventObject {

    private URL url;

    public DataEvent(Object path, URL url) {
        super(path);
        this.url = url;
    }

    /**
     * @return null表示删除，不为null表示修改
     */
    public URL getUrl() {
        return url;
    }

    @Override
    public String getSource() {
        return super.getSource().toString();
    }

    @Override
    public String toString() {
        return new StringBuilder("path: ").append(getSource()).append(", url: ").append(url).toString();
    }
}
