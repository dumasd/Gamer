package com.thinkerwolf.gamer.core.servlet;

/**
 * Session 管理器
 *
 * @author wukai
 */
public class SessionManager {


    private class SessionCheckThread extends Thread {


        @Override
        public void run() {
            for (; ; ) {


                try {
                    // 1s检查一次session
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
            }
        }

    }


}
