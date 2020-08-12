package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.util.ResourceUtils;
import org.junit.Test;

import javax.net.ssl.KeyManagerFactory;
import java.security.KeyStore;

public class SSLTests {

    @Test
    public void testKeyStore() {
        try {
            char[] passpharse = "Wk1234lc".toCharArray();
            KeyStore ksKeys = KeyStore.getInstance(KeyStore.getDefaultType());
            ksKeys.load(ResourceUtils.findInputStream("", "C:\\Users\\wukai\\gamer.keystore"), passpharse);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ksKeys, passpharse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
