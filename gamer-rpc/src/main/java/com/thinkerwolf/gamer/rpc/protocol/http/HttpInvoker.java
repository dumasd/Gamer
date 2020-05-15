package com.thinkerwolf.gamer.rpc.protocol.http;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.rpc.*;
import okhttp3.*;
import okhttp3.Response;

import java.io.IOException;

@SuppressWarnings("unchecked")
public class HttpInvoker<T> implements Invoker<T> {

    private static MediaType BYTES = MediaType.get("application/octet-stream");
    private URL url;
    private OkHttpClient client;

    public HttpInvoker(URL url, OkHttpClient client) {
        this.url = url;
        this.client = client;
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        // http请求
        RpcMessage msg = (RpcMessage) args;
        String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());

        RpcRequest rpcArgs = new RpcRequest();
        rpcArgs.setArgs(msg.getParameters());
        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);

        RequestBody requestBody = RequestBody.create(Serializations.getBytes(serializer, rpcArgs), BYTES);
        Request request = new Request.Builder().url(url.getHostPort() + "/" + command)
                .post(requestBody).build();
        DefaultPromise promise = new DefaultPromise();

        RpcResponse rpcResponse;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                promise.setFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    RpcResponse rpcResponse = Serializations.getObject(serializer, responseBody.bytes(), RpcResponse.class);
                    promise.setSuccess(rpcResponse);
                } catch (Exception e) {
                    promise.setFailure(e);
                }
            }
        });

        if (!msg.isAsync()) {
            promise.await();
            if (promise.cause() != null) {
                return new Result(promise.cause());
            }
            rpcResponse = (RpcResponse) promise.getNow();
            return new Result(rpcResponse.getResult());

        }

        RpcContext.getContext().setCurrent(promise);
        rpcResponse = (RpcResponse) promise.getNow();
        return rpcResponse == null ? new Result(null) : new Result(rpcResponse.getResult());
    }
}
