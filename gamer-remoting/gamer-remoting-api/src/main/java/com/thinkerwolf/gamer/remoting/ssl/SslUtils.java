package com.thinkerwolf.gamer.remoting.ssl;

import com.thinkerwolf.gamer.common.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;

public final class SslUtils {

    private static final char[] EMPTY_PASS = new char[0];

    public static KeyManagerFactory createKmf(SslConfig cfg) {
        try {
            char[] pass = cfg.getKeystorePass() == null ? EMPTY_PASS : cfg.getKeystorePass().toCharArray();
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            if (cfg.getKeystoreFile() == null) {
                return null;
            }
            InputStream is = ResourceUtils.findInputStream("", cfg.getKeystoreFile());
            if (is == null) {
                throw new FileNotFoundException(cfg.getKeystoreFile());
            }
            keyStore.load(is, pass);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, pass);
            return keyManagerFactory;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("KeyStoreManager", e);
        }
    }

}
