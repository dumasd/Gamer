package com.thinkerwolf.gamer.rpc.http;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Promise;
import com.thinkerwolf.gamer.common.serialization.Serializations;
import com.thinkerwolf.gamer.common.serialization.Serializer;
import com.thinkerwolf.gamer.remoting.ExchangeClient;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.thinkerwolf.gamer.common.Constants.REQUEST_TIMEOUT;
import static com.thinkerwolf.gamer.common.Constants.RPC_CLIENT_NUM;

public class HttpExchangeClient implements ExchangeClient<RpcResponse> {
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 10;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);
    private static final Map<String, CloseableHttpClient> httpClientCache =
            new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private URL url;
    private CloseableHttpClient httpClient;
    private final RequestConfig requestConfig;
    private final long timeout;

    public HttpExchangeClient(URL url) {
        this.url = url;
        this.timeout = url.getIntParameter(REQUEST_TIMEOUT, 2000);
        this.requestConfig =
                RequestConfig.custom().setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();
        initHttpClient();
    }

    private void initHttpClient() {
        this.httpClient =
                httpClientCache.computeIfAbsent(
                        url.toHostPort(),
                        k -> {
                            Registry<ConnectionSocketFactory> schemeRegistry =
                                    RegistryBuilder.<ConnectionSocketFactory>create()
                                            .register(
                                                    "http",
                                                    PlainConnectionSocketFactory.getSocketFactory())
                                            .register(
                                                    "https",
                                                    SSLConnectionSocketFactory.getSocketFactory())
                                            .build();
                            PoolingHttpClientConnectionManager connManager =
                                    new PoolingHttpClientConnectionManager(schemeRegistry);
                            connManager.setDefaultConnectionConfig(ConnectionConfig.DEFAULT);
                            connManager.setDefaultSocketConfig(
                                    SocketConfig.custom().setTcpNoDelay(true).build());
                            Integer maxConn = url.getIntParameter(RPC_CLIENT_NUM);
                            if (maxConn == null) {
                                maxConn =
                                        url.getAttach(
                                                RPC_CLIENT_NUM, DEFAULT_MAX_TOTAL_CONNECTIONS);
                            }
                            connManager.setMaxTotal(maxConn);
                            connManager.setDefaultMaxPerRoute(maxConn);

                            CloseableHttpClient httpClient =
                                    HttpClients.custom().setConnectionManager(connManager).build();
                            scheduler.scheduleAtFixedRate(
                                    new TimerTask() {
                                        @Override
                                        public void run() {
                                            connManager.closeExpiredConnections();
                                            connManager.closeIdleConnections(
                                                    30000, TimeUnit.MILLISECONDS);
                                        }
                                    },
                                    3000,
                                    3000,
                                    TimeUnit.MILLISECONDS);
                            return httpClient;
                        });
    }

    @Override
    public Promise<RpcResponse> request(Object message) {
        return request(message, 0, null);
    }

    @Override
    public Promise<RpcResponse> request(Object message, long timeout, TimeUnit unit) {
        DefaultPromise<RpcResponse> promise = new DefaultPromise<>();

        Invocation msg = (Invocation) message;
        String command =
                RpcUtils.getRpcCommand(
                        msg.getInterfaceClass(), msg.getMethodName(), msg.getParameterTypes());

        Serializer serializer = ServiceLoader.getService(msg.getSerial(), Serializer.class);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setArgs(msg.getParameters());
        rpcRequest.setAttachments(RpcContext.getContext().getAttachments());
        byte[] entityBytes;
        try {
            entityBytes = Serializations.getBytes(serializer, rpcRequest);
        } catch (IOException e) {
            promise.setFailure(e);
            return promise;
        }

        HttpPost httpPost = new HttpPost(url.toProtocolHostPort() + "/" + command);
        httpPost.setConfig(requestConfig);
        httpPost.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpPost.setEntity(new ByteArrayEntity(entityBytes));

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
        return promise;
    }
}
