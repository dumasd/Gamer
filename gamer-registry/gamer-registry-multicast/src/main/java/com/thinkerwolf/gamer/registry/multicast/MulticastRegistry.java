package com.thinkerwolf.gamer.registry.multicast;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.retry.RetryLoops;
import com.thinkerwolf.gamer.common.retry.RetryNTimes;
import com.thinkerwolf.gamer.common.util.NetUtils;
import com.thinkerwolf.gamer.registry.AbstractRegistry;
import com.thinkerwolf.gamer.registry.DataEvent;
import com.thinkerwolf.gamer.registry.RegistryException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static com.thinkerwolf.gamer.common.URL.*;
import static java.nio.charset.StandardCharsets.UTF_8;

public class MulticastRegistry extends AbstractRegistry {

    private static final int DEFAULT_PORT = 1234;

    private static final String REGISTER = "REGISTER";
    private static final String UNREGISTER = "UNREGISTER";

    private static final Logger LOG = InternalLoggerFactory.getLogger(MulticastRegistry.class);

    private final MulticastSocket multicastSocket;
    private final Set<URL> registered = new CopyOnWriteArraySet<>();
    private final ScheduledExecutorService resendExecutor;
    private InetAddress multicastAddress;
    private int multicastPort;

    public MulticastRegistry(URL url) {
        super(url);
        this.multicastSocket = prepareClient(url);
        this.resendExecutor = Executors.newSingleThreadScheduledExecutor();
        try {
            final int ttl = multicastSocket.getTimeToLive();
            resendExecutor.scheduleAtFixedRate(
                    () -> {
                        if (!multicastSocket.isClosed()) {
                            for (URL u : registered) {
                                multicast(REGISTER + " " + u.toString());
                            }
                        }
                    },
                    ttl,
                    ttl,
                    TimeUnit.SECONDS);
        } catch (IOException e) {
            throw new RegistryException(e);
        }

        Thread thread =
                new Thread(
                        () -> {
                            byte[] buff = new byte[2048];
                            final DatagramPacket packet = new DatagramPacket(buff, buff.length);
                            while (!multicastSocket.isClosed()) {
                                try {
                                    multicastSocket.receive(packet);
                                    String msg = new String(packet.getData());
                                    int idx = msg.indexOf('\n');
                                    if (idx > 0) {
                                        msg = msg.substring(0, idx);
                                    }
                                    MulticastRegistry.this.received(msg, packet.getSocketAddress());
                                    Arrays.fill(buff, (byte) 0);
                                } catch (Throwable e) {
                                    if (multicastSocket.isClosed()) {
                                        LOG.error("Closed", e);
                                    }
                                }
                            }
                        },
                        "MulticastRegistry");
        thread.setDaemon(true);
        thread.start();
    }

    private MulticastSocket prepareClient(URL url) {
        final int connectionTimeout =
                url.getInteger(CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
        int retry = url.getInteger(RETRY, DEFAULT_RETRY_TIMES);
        try {
            this.multicastAddress = InetAddress.getByName(url.getHost());
            this.multicastPort = url.getPort() <= 0 ? DEFAULT_PORT : url.getPort();
            return RetryLoops.invokeWithRetry(
                    () -> {
                        MulticastSocket socket = new MulticastSocket(multicastPort);
                        socket.setTimeToLive(30);
                        NetUtils.joinMulticastGroup(socket, multicastAddress);
                        return socket;
                    },
                    new RetryNTimes(retry, connectionTimeout + 50, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            throw new RegistryException(e);
        }
    }

    @Override
    protected void doRegister(URL url) {
        registered.add(url);
        multicast(REGISTER + " " + url.toString());
        fireDataChange(new DataEvent(toPathName(url), url));
    }

    @Override
    protected void doUnRegister(URL url) {
        registered.remove(url);
        multicast(UNREGISTER + " " + url.toString());
        fireDataChange(new DataEvent(toPathName(url), null));
    }

    @Override
    protected void doSubscribe(URL url) {
        // Do nothing
    }

    @Override
    protected void doUnSubscribe(URL url) {
        // Do nothing
    }

    @Override
    protected List<URL> doLookup(URL url) {
        return Collections.emptyList();
    }

    private void multicast(String msg) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Send multicast message {} to {}:{}", msg, multicastAddress, multicastPort);
        }
        byte[] buff = (msg + "\n").getBytes(UTF_8);
        DatagramPacket packet =
                new DatagramPacket(buff, buff.length, multicastAddress, multicastPort);
        try {
            multicastSocket.send(packet);
        } catch (Exception e) {
            throw new RegistryException("Multicast [" + url + "]", e);
        }
    }

    private void received(String msg, SocketAddress socketAddress) {
        LOG.info("Multicast received {}", msg);
        if (msg.startsWith(REGISTER)) {
            URL url = URL.parse(msg.substring(REGISTER.length() + 1));
            DataEvent dataEvent = new DataEvent(toPathName(url), url);
            fireDataChange(dataEvent);
        } else if (msg.startsWith(UNREGISTER)) {
            URL url = URL.parse(msg.substring(UNREGISTER.length() + 1));
            DataEvent dataEvent = new DataEvent(toPathName(url), null);
            fireDataChange(dataEvent);
        }
    }

    private void clean() {
        for (URL url : registered) {
            unregister(url);
        }
    }

    @Override
    public void close() {
        if (multicastSocket.isClosed()) {
            return;
        }
        clean();
        resendExecutor.shutdown();
        multicastSocket.close();
    }
}
