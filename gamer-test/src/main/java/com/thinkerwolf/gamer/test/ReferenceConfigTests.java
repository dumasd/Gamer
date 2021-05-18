package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.rpc.ReferenceConfig;
import com.thinkerwolf.gamer.rpc.RpcCallback;
import com.thinkerwolf.gamer.rpc.RpcContext;
import com.thinkerwolf.gamer.rpc.exception.RpcException;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import org.apache.commons.lang.RandomStringUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** @author wukai */
public class ReferenceConfigTests {

    private static IRpcAction testRegistry(String url) {
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setRegistry(url);
        return ref.get();
    }

    private static IRpcAction testDirect(String url) {
        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setUrl(url);
        return ref.get();
    }

    public static void main(String[] args) {
        //        String url = "tcp://localhost:9090?clientNum=30";
        //        String url = "tcp://192.168.1.3:9090?clientNum=5";
        //        IRpcAction rpcAction = testRegistry("zookeeper://localhost:2181/test");
        IRpcAction rpcAction = testRegistry("etcd://localhost:2379/test");
        //        System.err.println(rpcAction.sayHello("wukai"));
        //        System.err.println(rpcAction.getList());

        testSequence(rpcAction, 100);
        testConcurrency(rpcAction, 30, new AtomicInteger());
        try {
            TimeUnit.MILLISECONDS.sleep(10000);
        } catch (InterruptedException ignored) {
        }
        testConcurrency(rpcAction, 100, new AtomicInteger());
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
                                            rpcAction.sayHelloAsync(
                                                    "wukai-"
                                                            + RandomStringUtils.randomAlphanumeric(
                                                                    5));
                                    RpcContext.getContext()
                                            .addListener(
                                                    new RpcCallback<String>() {
                                                        @Override
                                                        protected void onSuccess(String result)
                                                                throws Exception {
                                                            System.out.println(result);
                                                        }

                                                        @Override
                                                        protected void onBusinessError(Throwable t)
                                                                throws Exception {}

                                                        @Override
                                                        protected void onRpcError(RpcException ex)
                                                                throws Exception {}
                                                    });

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
