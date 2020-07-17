package com.thinkerwolf.gamer.rpc.http;

import com.thinkerwolf.gamer.common.DefaultThreadFactory;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.common.util.ConcurrentUtils;
import com.thinkerwolf.gamer.rpc.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.thinkerwolf.gamer.common.URL.RPC_CLIENT_NUM;

public class HttpInvoker<T> implements Invoker<T> {
    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpInvoker.class);

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
    private static final Map<URL, CloseableHttpClient> httpClientCache = new ConcurrentHashMap<>();

    private final URL url;
    private CloseableHttpClient httpClient;
    private final RequestConfig requestConfig;
    private final ExecutorService executor;
    private final long timeout;

    public HttpInvoker(URL url) {
        this.url = url;
        this.timeout = url.getInteger(URL.REQUEST_TIMEOUT, 4000);
        this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .build();
        this.executor = ConcurrentUtils.newNormalExecutor(url, new DefaultThreadFactory("http-invoker", true));
        initHttpClient();
    }

    private void initHttpClient() {
        this.httpClient = httpClientCache.computeIfAbsent(url, url -> {
            Registry<ConnectionSocketFactory> schemeRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", SSLConnectionSocketFactory.getSocketFactory())
                    .build();
            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(schemeRegistry);
            connManager.setDefaultConnectionConfig(ConnectionConfig.DEFAULT);
            connManager.setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).build());
            Integer maxConn = url.getInteger(RPC_CLIENT_NUM);
            if (maxConn == null) {
                maxConn = url.getAttach(RPC_CLIENT_NUM, DEFAULT_MAX_TOTAL_CONNECTIONS);
            }
            connManager.setMaxTotal(maxConn);
            connManager.setDefaultMaxPerRoute(maxConn);
            return HttpClients.createMinimal(connManager);
        });
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage msg = (RpcMessage) args;
        String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());

        RpcRequest rpcArgs = new RpcRequest();
        rpcArgs.setArgs(msg.getParameters());
        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
        HttpPost httpPost = new HttpPost(url.getProtocolHostPort() + "/" + command);
        httpPost.setConfig(requestConfig);
        httpPost.setEntity(new ByteArrayEntity(Serializations.getBytes(serializer, rpcArgs)));

        DefaultPromise<RpcResponse> promise = new DefaultPromise<>();
        executor.execute(() -> {
            CloseableHttpResponse httpResponse = null;
            HttpEntity entity = null;
            try {
                httpResponse = httpClient.execute(httpPost);
                entity = httpResponse.getEntity();
                byte[] body = EntityUtils.toByteArray(entity);
                byte[] data = ArrayUtils.subarray(body, 4, body.length);
                RpcResponse rpcResponse = Serializations.getObject(serializer, data, RpcResponse.class);
                promise.setSuccess(rpcResponse);
            } catch (Exception e) {
                promise.setFailure(e);
            } finally {
                EntityUtils.consumeQuietly(entity);
                IOUtils.closeQuietly(httpResponse);
            }
        });

        if (!msg.getRpcClient().async()) {
            try {
                promise.await(timeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOG.warn("", e);
            }
            if (!promise.isDone()) {
                promise.setFailure(new TimeoutException());
            }
            if (promise.isSuccess()) {
                promise.getNow();
                return new Result(promise.getNow().getResult());
            } else {
                return new Result(promise.cause());
            }
        } else {
            if (promise.isSuccess()) {
                return new Result(promise.getNow().getResult());
            }
            return new Result((Object) null);
        }
    }
}
