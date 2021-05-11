package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.rpc.ReferenceConfig;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import org.apache.commons.lang.RandomStringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/** @author wukai */
public class ReferenceConfigTests {

    private static IRpcAction testRegistry() {
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setRegistry("zookeeper://localhost:2181/test");
        return ref.get();
    }

    private static IRpcAction testDirect(String url) {
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setUrl(url);
        return ref.get();
    }

    public static void main(String[] args) {
        String localURL = "tcp://localhost:9090?clientNum=20";
        String remoteURL = "tcp://192.168.1.3:9090?clientNum=5";
        IRpcAction rpcAction = testDirect(remoteURL);
        System.err.println(rpcAction.sayHello("wukai"));
        System.err.println(rpcAction.getList());

        testSequence(rpcAction, 100);
        //        testConcurrency(rpcAction, 30, new AtomicInteger());
        //        try {
        //            TimeUnit.MILLISECONDS.sleep(5000);
        //        } catch (InterruptedException ignored) {
        //        }
        //        testConcurrency(rpcAction, 100, new AtomicInteger());

    }

    private static void testSequence(IRpcAction rpcAction, int times) {
        for (int i = 0; i < times; i++) {
            int count = i + 1;
            long startTime = System.nanoTime();
            try {
                String r = rpcAction.sayHello("wukai-" + RandomStringUtils.randomAlphanumeric(5));
                long endTime = System.nanoTime();
                double spend = (double) (endTime - startTime) / 1000000;

                System.err.println("Count::: " + count + ", Time:::" + spend + ", Result::: " + r);
            } catch (Exception e) {
                long endTime = System.nanoTime();
                double spend = (double) (endTime - startTime) / 1000000;
                System.err.println("Count::: " + count + ", Time::: " + spend + ", Exception:::");
                e.printStackTrace();
            } finally {

            }
        }
    }

    private static void testConcurrency(
            IRpcAction rpcAction, int times, final AtomicInteger counter) {
        for (int i = 0; i < times; i++) {
            Thread t =
                    new Thread(
                            () -> {
                                int count = counter.incrementAndGet();
                                long startTime = System.nanoTime();
                                try {
                                    String r =
                                            rpcAction.sayHello(
                                                    "wukai-"
                                                            + RandomStringUtils.randomAlphanumeric(
                                                                    5));
                                    long endTime = System.nanoTime();
                                    double spend = (double) (endTime - startTime) / 1000000;

                                    System.err.println(
                                            "Count::: "
                                                    + count
                                                    + ", Time:::"
                                                    + spend
                                                    + ", Result::: "
                                                    + r);

                                } catch (Exception e) {
                                    long endTime = System.nanoTime();
                                    double spend = (double) (endTime - startTime) / 1000000;
                                    System.err.println(
                                            "Count::: "
                                                    + count
                                                    + ", Time::: "
                                                    + spend
                                                    + ", Exception:::");
                                    e.printStackTrace();
                                } finally {

                                }
                            });
            t.start();
        }
    }
}
