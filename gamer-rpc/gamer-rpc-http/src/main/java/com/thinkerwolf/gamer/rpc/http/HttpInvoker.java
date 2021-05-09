package com.thinkerwolf.gamer.rpc.http;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.rpc.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
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

import java.io.IOException;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.*;

import static com.thinkerwolf.gamer.common.URL.RPC_CLIENT_NUM;

public class HttpInvoker<T> implements Invoker<T> {
    private static final Logger LOG = InternalLoggerFactory.getLogger(HttpInvoker.class);

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 10;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
    private static final Map<String, CloseableHttpClient> httpClientCache = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

    private final URL url;
    private CloseableHttpClient httpClient;
    private final RequestConfig requestConfig;
    private final long timeout;

    public HttpInvoker(URL url) {
        this.url = url;
        this.timeout = url.getInteger(URL.REQUEST_TIMEOUT, 2000);
        this.requestConfig = RequestConfig.custom()
                .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .build();
        initHttpClient();

    }

    private void initHttpClient() {
        this.httpClient = httpClientCache.computeIfAbsent(url.toHostPort(), k -> {
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

            CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connManager).build();
            scheduled.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    connManager.closeExpiredConnections();
                    connManager.closeIdleConnections(30000, TimeUnit.MILLISECONDS);
                }
            }, 3000, 3000, TimeUnit.MILLISECONDS);
            return httpClient;
        });
    }

    @Override
    public Result invoke(Object args) throws Throwable {
        RpcMessage msg = (RpcMessage) args;
        String command = RpcUtils.getRpcCommand(msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());

        RpcRequest rpcArgs = new RpcRequest();
        rpcArgs.setArgs(msg.getParameters());
        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
        HttpPost httpPost = new HttpPost(url.toProtocolHostPort() + "/" + command);
        httpPost.setConfig(requestConfig);
        httpPost.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpPost.setEntity(new ByteArrayEntity(Serializations.getBytes(serializer, rpcArgs)));

        DefaultPromise<RpcResponse> promise = new DefaultPromise<>();
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
        if (!msg.isAsync()) {
            if (!promise.isDone()) {
                promise.setFailure(new TimeoutException());
            }
            return RpcUtils.processSync(promise);
        } else {
            RpcContext.getContext().setCurrent(promise);
            return RpcUtils.processAsync(promise);
        }
    }

    @Override
    public void destroy() {
        CloseableHttpClient httpClient = httpClientCache.remove(url.toHostPort());
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException ignored) {
            }
        }
    }
}
