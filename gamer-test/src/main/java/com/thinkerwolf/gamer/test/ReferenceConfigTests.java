package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.ReferenceConfig;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import org.apache.commons.lang.RandomStringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceConfigTests {

    private static Thread.UncaughtExceptionHandler threadExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.err.println("Thread - " + t.getName() + " caught exception " + e.getClass().getName());
        }
    };

    public static void main(String[] args) {
       String url = "ws://101.200.177.204:80?clientNum=5";
//        String url = "http://127.0.0.1:8080";
//        String url = "ws://127.0.0.1:8080?clientNum=5";
//        String url = "tcp://127.0.0.1:8090?clientNum=5";
        URL u = URL.parse(url);
//        String url = "tcp://127.0.0.1:8090";
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setUrl(url);

        IRpcAction rpcAction = ref.get();

        System.err.println(rpcAction.sayHello("wukai"));
        System.err.println(rpcAction.getList());

        AtomicInteger counter = new AtomicInteger();
        testConcurrency(rpcAction, 60, counter);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        testConcurrency(rpcAction, 80, counter);
    }


    private static void testConcurrency(IRpcAction rpcAction, int times, final AtomicInteger counter) {
        for (int i = 0; i < times; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String r = rpcAction.sayHello("wukai-" + RandomStringUtils.random(5));
                    System.err.println("Count::: " + counter.incrementAndGet() + ", Result::: "  + r);
                }
            });
            t.setUncaughtExceptionHandler(threadExceptionHandler);
            t.start();
        }
    }


}
