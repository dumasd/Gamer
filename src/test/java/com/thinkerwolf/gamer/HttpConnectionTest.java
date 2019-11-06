package com.thinkerwolf.gamer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpConnectionTest {

    public static final String url = "http://localhost:8080/longhttp?pin=123";

    public static void main(String[] args) throws Exception {

        ExecutorService es = Executors.newFixedThreadPool(1);
        for (int i = 0; i < 1; i++) {
            es.submit(new Runnable() {
                public void run() {
                    String s = url + "&client_id=" + UUID.randomUUID().toString();
                    connection(s);
                }
            });
        }
    }

    static void connection(String url) {

        InputStream is = null;
        HttpURLConnection conn = null;
        byte[] buf = new byte[1024];
        for (; ; ) {
            try {
                java.net.URL a = new URL(url);
                conn = (HttpURLConnection) a.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.connect();
                is = conn.getInputStream();
                int ret = 0;
                conn.getHeaderField("Set-Cookie");
                while ((ret = is.read(buf)) > 0) {
                    processBuf(buf, ret);
                }
                // close the inputstream
                is.close();
            } catch (IOException e) {
                try {
                    int respCode = ((HttpURLConnection) conn).getResponseCode();
                    InputStream es = ((HttpURLConnection) conn).getErrorStream();
                    int ret = 0;
                    // read the response body
                    while ((ret = es.read(buf)) > 0) {
                        processBuf(buf, ret);
                    }
                    // close the errorstream
                    es.close();
                } catch (IOException ex) {
                    e.printStackTrace();
                }
            }
        }


    }

    static void processBuf(byte[] buf, int length) {
        System.out.println(new String(buf, 0, length));
    }
}























































