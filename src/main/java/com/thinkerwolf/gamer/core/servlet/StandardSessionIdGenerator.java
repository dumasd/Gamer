package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StandardSessionIdGenerator implements SessionIdGenerator {

    private static final Logger LOG = InternalLoggerFactory.getLogger(StandardSessionIdGenerator.class);

    private String jvmRoute;

    private volatile int sessionIdLength = 16;

    private Queue<SecureRandom> randomQueue = new ConcurrentLinkedQueue<>();

    private String secureRandomAlgorithm = "SHA1PRNG";

    private String secureRandomProvider;

    @Override
    public void setJvmRoute(String jvmRoute) {
        this.jvmRoute = jvmRoute;
    }

    @Override
    public void setSessionIdLength(int sessionIdLength) {
        this.sessionIdLength = sessionIdLength;
    }

    @Override
    public int getSessionIdLength() {
        return sessionIdLength;
    }

    @Override
    public String generateSessionId() {
        return generateSessionId(null);
    }

    public String getSecureRandomAlgorithm() {
        return secureRandomAlgorithm;
    }

    public void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
        this.secureRandomAlgorithm = secureRandomAlgorithm;
    }

    public String getSecureRandomProvider() {
        return secureRandomProvider;
    }

    public void setSecureRandomProvider(String secureRandomProvider) {
        this.secureRandomProvider = secureRandomProvider;
    }

    @Override
    public String generateSessionId(String route) {
        byte[] bytes = new byte[16];
        int idLength = 0;
        StringBuilder id = new StringBuilder();
        while (idLength < sessionIdLength) {
            getRandomBytes(bytes);
            for (int i = 0, len = bytes.length; i < len && idLength < sessionIdLength; i++) {
                byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
                byte b2 = (byte) (bytes[i] & 0x0f);
                if (b1 < 10) {
                    id.append((char) ('0' + b1));
                } else {
                    id.append((char) ('A' + (b1 - 10)));
                }
                if (b2 < 10) {
                    id.append((char) ('0' + b2));
                } else {
                    id.append((char) ('A' + (b2 - 10)));
                }
                idLength++;
            }
        }
        if (route != null && route.length() > 0) {
            id.append('.').append(route);
        } else if (jvmRoute != null && jvmRoute.length() > 0) {
            id.append('.').append(route);
        }

        return id.toString();
    }

    protected void getRandomBytes(byte[] bytes) {
        getRandom().nextBytes(bytes);
    }

    private SecureRandom getRandom() {
        SecureRandom random = randomQueue.poll();
        if (random == null) {
            random = createRandom();
        }
        randomQueue.add(random);
        return random;
    }

    private SecureRandom createRandom() {
        long t1 = System.currentTimeMillis();
        SecureRandom result = null;
        boolean error = false;
        try {
            if (secureRandomProvider != null && secureRandomProvider.length() > 0) {
                result = SecureRandom.getInstance(secureRandomAlgorithm, secureRandomProvider);
            } else if (secureRandomAlgorithm != null && secureRandomAlgorithm.length() > 0) {
                result = SecureRandom.getInstance(secureRandomAlgorithm);
            }
        } catch (NoSuchAlgorithmException e) {
            error = true;
            LOG.error("SecureRandom algorithm : " + secureRandomAlgorithm, e);
        } catch (NoSuchProviderException e) {
            error = true;
            LOG.error("SecureRandom provider : " + secureRandomProvider, e);
        }

        if (result == null && error) {
            try {
                result = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                LOG.error("SecureRandom algorithm : SHA1PRNG", e);
            }
        }

        if (result == null) {
            result = new SecureRandom();
        }
        // force seeding take place
        result.nextInt(); // 非常耗时700ms
        long t = System.currentTimeMillis() - t1;
        if (t > 100) {
            LOG.warn("SecureRandom generating spend to long : " + result.getAlgorithm() + "[" + t + "]");
        }

        return result;
    }

}
