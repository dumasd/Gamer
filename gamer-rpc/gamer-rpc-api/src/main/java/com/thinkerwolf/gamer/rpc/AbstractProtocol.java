package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.URL;

public abstract class AbstractProtocol implements Protocol {

    @Override
    public <T> Invoker<T> invoker(Class<T> interfaceClass, URL url) {
        URL newUrl = processUrl(url);
        return doInvoker(interfaceClass, newUrl);
    }

    protected abstract <T> Invoker<T> doInvoker(Class<T> interfaceClass, URL url);

    protected URL processUrl(URL url) {
        return RpcUtils.getConnectUrl(url);
    }

}
