package com.thinkerwolf.gamer.core.ssl;

import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.remoting.ssl.SslConfig;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

/**
 * 创建SSLContext
 *
 * @author wukai
 * @date 2020/5/13 16:02
 */
public final class SocketSslContextFactory {

    public static final String PROTOCOL = "TLS";


    public static SSLContext createServerContext(SslConfig cfg) {
        try {
            String algorithm = System.getProperty("ssl.KeyManagerFactory.algorithm");
            if (algorithm == null) {
                algorithm = "SunX509";
            }
            char[] passpharse = cfg.getKeystorePass().toCharArray();
            KeyStore ksKeys = KeyStore.getInstance(KeyStore.getDefaultType());
            ksKeys.load(ResourceUtils.findInputStream("", cfg.getKeystoreFile()), passpharse);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ksKeys, passpharse);
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
            sslContext.init(kmf.getKeyManagers(), null, null);
            return sslContext;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Create server ssl error", e);
        }
    }


    public static SSLContext createClientContext(SslConfig cfg) {
        String algorithm = System.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }
        try {
            char[] password = cfg.getKeystorePass().toCharArray();
            KeyStore ksTrust = KeyStore.getInstance("JKS");
            ksTrust.load(ResourceUtils.findInputStream("", cfg.getTruststoreFile()), password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
            tmf.init(ksTrust);
            SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Create server ssl error", e);
        }
    }


}
