package com.thinkerwolf.gamer.rpc.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.Invoker;
import com.thinkerwolf.gamer.rpc.Protocol;

/**
 * @author wukai
 * @since 2020-06-11
 */
public class HttpProtocol implements Protocol {

    @Override
    public <T> Invoker<T> invoker(Class<T> interfaceClass, URL url) {
        HttpInvoker<T> invoker = new HttpInvoker<>(url);
        return invoker;
    }

}
