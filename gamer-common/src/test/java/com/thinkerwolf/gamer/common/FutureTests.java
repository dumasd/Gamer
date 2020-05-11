package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.concurrent.DefaultPromise;
import com.thinkerwolf.gamer.common.concurrent.Future;
import com.thinkerwolf.gamer.common.concurrent.FutureListener;
import org.apache.commons.lang.math.RandomUtils;

import java.util.concurrent.*;

public class FutureTests {

    private static Executor executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        final DefaultPromise promise = extcute();

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                try {
                    long m = RandomUtils.nextInt(2000) + 2000;
                    Object obj = promise.get(m, TimeUnit.MILLISECONDS);
                    System.out.println(Thread.currentThread() + " > " + obj);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            });
            t.start();
        }
        promise.addListener(new FutureListener() {
            @Override
            public void operationComplete(Future future) throws Throwable {
                System.err.println("Listener > " + future.get());
            }
        });
        try {
            Object obj = promise.get();
            if (!promise.isSuccess()) {
                if (promise.cause() != null) {
                    promise.cause().printStackTrace();
                }
            }
            System.out.println(Thread.currentThread() + " > " + obj);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    public static DefaultPromise extcute() {
        final DefaultPromise promise = new DefaultPromise();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {

                }
                promise.setFailure(new RuntimeException());
                //promise.setSuccess(3213);
            }
        });
        return promise;
    }

}
