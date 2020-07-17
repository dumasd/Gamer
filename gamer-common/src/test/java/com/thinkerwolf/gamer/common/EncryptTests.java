package com.thinkerwolf.gamer.common;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import javax.crypto.*;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @since 2020-07-16
 */
public class EncryptTests {

    @Test
    public void testDES() throws Exception {
        // 64BIT密钥（8字节） 加密64BIT明文（8字节）生成64BIT（8字节）密文
        String transformation = "DES";
        System.out.println("\nTest cipher " + transformation + " ========================");
        KeyGenerator kg = KeyGenerator.getInstance(transformation);
        SecretKey secretKey = kg.generateKey();
        String keyStr = new String(Base64.getEncoder().encode(secretKey.getEncoded()));
        System.out.println("SecretKey : " + keyStr + " ||| KeyLength : " + secretKey.getEncoded().length);

        byte[] encBs = encrypt(transformation, secretKey, "jackson".getBytes(UTF_8));
        System.out.println("EncryptText :" + new String(encBs, UTF_8));

        byte[] decBs = decrypt(transformation, secretKey, encBs);
        System.out.println("DecryptText :" + new String(decBs, UTF_8));
    }

    @Test
    public void testAES() throws Exception {
        // 128BIT密钥（16字节）加密128BIT明文（16字节）生成128BIT（16字节）密文
        String transformation = "AES";
        System.out.println("\nTest cipher " + transformation + " ========================");
        KeyGenerator kg = KeyGenerator.getInstance(transformation);
        SecretKey secretKey = kg.generateKey();
        String keyStr = new String(Base64.getEncoder().encode(secretKey.getEncoded()));
        System.out.println("SecretKey : " + keyStr + " ||| KeyLength : " + secretKey.getEncoded().length);

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            int c = 'a' + RandomUtils.nextInt(26);
            s.append((char) c);
        }
        System.out.println("PlainText : " + s);

        byte[] encBs = encrypt(transformation, secretKey, s.toString().getBytes(UTF_8));
        System.out.println("EncryptText : " + new String(encBs, UTF_8));

        byte[] decBs = decrypt(transformation, secretKey, encBs);
        System.out.println("DecryptText : " + new String(decBs, UTF_8));
    }

    @Test
    public void testRSA() throws Exception {
        String transformation = "RSA";
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(transformation);
        KeyPair keyPair = kpg.generateKeyPair();



    }


    private static byte[] encrypt(String transformation, Key key, byte[] plainBytes)
            throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plainBytes);
    }

    private static byte[] decrypt(String transformation, Key key, byte[] cipherBytes)
            throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherBytes);
    }
}
