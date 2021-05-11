package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.rpc.ReferenceConfig;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import org.apache.commons.lang.RandomStringUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class ReferenceConfigTests {

    private static IRpcAction testRegistry() {
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setRegistry("zookeeper://localhost:2181/test");
        return ref.get();
    }

    private static IRpcAction testURL() {
        String url = "ws://101.200.177.204:80?clientNum=5";
        //        String url = "http://127.0.0.1:8080";
        //        String url = "ws://127.0.0.1:8080?clientNum=5";
        //        String url = "tcp://127.0.0.1:8090?clientNum=5";
        URL u = URL.parse(url);
        //        String url = "tcp://127.0.0.1:8090";
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setUrl(url);
        return ref.get();
    }

    public static void main(String[] args) {

        IRpcAction rpcAction = testRegistry();

        System.err.println(rpcAction.sayHello("wukai"));
        System.err.println(rpcAction.getList());

        testConcurrency(rpcAction, 40, new AtomicInteger());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {
        }

        // testConcurrency(rpcAction, 80, counter);
    }

    private static void testConcurrency(
            IRpcAction rpcAction, int times, final AtomicInteger counter) {
        for (int i = 0; i < times; i++) {
            Thread t =
                    new Thread(
                            () -> {
                                int count = counter.incrementAndGet();
                                try {
                                    String r =
                                            rpcAction.sayHello(
                                                    "wukai-" + RandomStringUtils.random(5));
                                    System.err.println("Count::: " + count + ", Result::: " + r);
                                } catch (Exception e) {
                                    System.err.println("Count::: " + count + ", Exception:::");
                                    e.printStackTrace();
                                }
                            });
            t.start();
        }
    }
}
