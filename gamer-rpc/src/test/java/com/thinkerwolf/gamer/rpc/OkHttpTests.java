package com.thinkerwolf.gamer.rpc;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class OkHttpTests {
    @Test
    public void testBasic() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://127.0.0.1:8080/test@hello?name=wukai")
                .get().build();

        CountDownLatch latch = new CountDownLatch(2);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                latch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(response.body().string());
                latch.countDown();
            }
        });

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                latch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println(response.body().string());
                latch.countDown();
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
        }
    }

}
