package com.thinkerwolf.gamer.rpc.protocol.http;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.rpc.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class HttpInvoker<T> implements Invoker<T> {

    private final URL url;
    private final CloseableHttpClient httpClient;

    public HttpInvoker(URL url) {
        this.url = url;
        this.httpClient = HttpClients.createDefault();
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage msg = (RpcMessage) args;
        String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());

        RpcRequest rpcArgs = new RpcRequest();
        rpcArgs.setArgs(msg.getParameters());
        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
        try {
            HttpPost httpPost = new HttpPost(url.getProtocolHostPort() + "/" + command);
            httpPost.setEntity(new ByteArrayEntity(Serializations.getBytes(serializer, rpcArgs)));
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            byte[] body = EntityUtils.toByteArray(httpResponse.getEntity());
            byte[] data = ArrayUtils.subarray(body, 4, body.length);
            RpcResponse rpcResponse = Serializations.getObject(serializer, data, RpcResponse.class);
            return new Result(rpcResponse.getResult());
        } catch (Exception e) {
            return new Result(e);
        }
    }
}
