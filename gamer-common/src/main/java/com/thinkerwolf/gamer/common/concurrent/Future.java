package com.thinkerwolf.gamer.common.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * future
 *
 * @author wukai
 * @date 2020/5/11 13:14
 */
public interface Future<V> extends java.util.concurrent.Future<V> {

    boolean isSuccess();

    Future<V> await() throws InterruptedException;

    Future<V> await(long time, TimeUnit unit) throws InterruptedException;

    Throwable cause();

    V getNow();
}
