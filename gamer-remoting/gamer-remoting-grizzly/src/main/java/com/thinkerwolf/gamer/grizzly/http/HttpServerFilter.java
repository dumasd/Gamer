package com.thinkerwolf.gamer.grizzly.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.grizzly.GrizzlyServerFilter;
import com.thinkerwolf.gamer.remoting.ChannelHandler;

public class HttpServerFilter extends GrizzlyServerFilter {

    public HttpServerFilter(URL url, ChannelHandler handler) {
        super(url, handler);
    }

}
