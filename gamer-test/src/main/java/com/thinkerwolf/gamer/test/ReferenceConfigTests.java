package com.thinkerwolf.gamer.test;

import com.thinkerwolf.gamer.rpc.ReferenceConfig;
import com.thinkerwolf.gamer.test.action.IRpcAction;
import org.apache.commons.lang.RandomStringUtils;

public class ReferenceConfigTests {

    private static Thread.UncaughtExceptionHandler threadExceptionHandler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.err.println("Thread - " + t.getName() + " caught exception");
        }
    };

    public static void main(String[] args) {

        ReferenceConfig<IRpcAction> ref = new ReferenceConfig<>();
        ref.setInterfaceClass(IRpcAction.class);
        ref.setUrl("tcp://127.0.0.1:8090");

        IRpcAction rpcAction = ref.get();
        System.err.println(rpcAction.sayHello("wukai"));
        System.err.println(rpcAction.getList());

        testConcurrency(rpcAction, 60);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        testConcurrency(rpcAction, 80);
    }


    private static void testConcurrency(IRpcAction rpcAction, int times) {
        for (int i = 0; i < times; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    String r = rpcAction.sayHello("wukai-" + RandomStringUtils.random(5));
                    System.err.println(r);
                }
            });
            t.setUncaughtExceptionHandler(threadExceptionHandler);
            t.start();
        }
    }


}
