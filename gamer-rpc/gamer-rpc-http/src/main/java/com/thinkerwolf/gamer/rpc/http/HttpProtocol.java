package com.thinkerwolf.gamer.rpc.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.AbstractProtocol;
import com.thinkerwolf.gamer.rpc.Invoker;

/**
 * @author wukai
 * @since 2020-06-11
 */
public class HttpProtocol extends AbstractProtocol {

    @Override
    protected <T> Invoker<T> doInvoker(Class<T> interfaceClass, URL url) {
        return new HttpInvoker<>(url);
    }

}
