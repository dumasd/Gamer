package com.thinkerwolf.gamer.common.util;


import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.io.IOException;
import java.net.*;
import java.util.*;

import static com.thinkerwolf.gamer.common.Constants.GAMER_PREFERRED_NETWORK_INTERFACE;

/**
 * 网络工具
 *
 * @author wukai
 */
public final class NetUtils {
    private static final Logger LOG = InternalLoggerFactory.getLogger(NetUtils.class);

    private volatile static InetAddress LOCAL_ADDRESS;

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress result = null;
        try {
            NetworkInterface ni = findNetworkInterface();
            assert ni != null;
            Enumeration<InetAddress> addresses = ni.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> op = toValidAddress(addresses.nextElement());
                if (op.isPresent()) {
                    try {
                        if (op.get().isReachable(100)) {
                            result = op.get();
                            break;
                        }
                    } catch (IOException ignored) {
                        // ignored
                    }
                }
            }
        } catch (Throwable t) {
            LOG.warn("", t);
        }
        if (result == null) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                Optional<InetAddress> op = toValidAddress(address);
                if (op.isPresent()) {
                    result = op.get();
                }
            } catch (Throwable t) {
                LOG.warn("", t);
            }
        }
        if (result != null) {
            LOCAL_ADDRESS = result;
        }
        return result;
    }


    public static NetworkInterface findNetworkInterface() {
        List<NetworkInterface> nis = Collections.emptyList();
        NetworkInterface result = null;
        try {
            nis = obtainNetworkInterfaces();
            for (NetworkInterface ni : nis) {
                if (isPreferNetworkInterface(ni)) {
                    result = ni;
                }
            }
        } catch (Throwable t) {
            LOG.warn("", t);
        }

        if (result == null) {
            for (NetworkInterface ni : nis) {
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    Optional<InetAddress> op = toValidAddress(addresses.nextElement());
                    if (op.isPresent()) {
                        try {
                            if (op.get().isReachable(100)) {
                                result = ni;
                                break;
                            }
                        } catch (IOException ignored) {
                            // ignored
                        }
                    }
                }
            }
        }
        if (result == null) {
            result = nis.isEmpty() ? null : nis.get(0);
        }
        return result;
    }


    public static List<NetworkInterface> obtainNetworkInterfaces() throws SocketException {
        List<NetworkInterface> nis = new ArrayList<>();
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            NetworkInterface ni = e.nextElement();
            if (!ignoreNetworkInterface(ni)) {
                nis.add(ni);
            }
        }
        return nis;

    }

    private static Optional<InetAddress> toValidAddress(InetAddress address) {
        boolean ipv6 = isPreferIPv6Address();
        if (address instanceof Inet6Address) {
            return ipv6 ? Optional.of(address) : Optional.empty();
        }
        if (address instanceof Inet4Address) {
            return !ipv6 ? Optional.of(address) : Optional.empty();
        }
        return Optional.empty();
    }

    private static boolean ignoreNetworkInterface(NetworkInterface ni) throws SocketException {
        return ni == null || ni.isLoopback() || ni.isVirtual() || !ni.isUp();
    }

    private static boolean isPreferNetworkInterface(NetworkInterface ni) {
        String p = System.getProperty(GAMER_PREFERRED_NETWORK_INTERFACE);
        return Objects.equals(ni.getDisplayName(), p);
    }

    static boolean isPreferIPv6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }
}
