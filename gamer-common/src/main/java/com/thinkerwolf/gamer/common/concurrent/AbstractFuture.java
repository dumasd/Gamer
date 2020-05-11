package com.thinkerwolf.gamer.common.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wukai
 * @date 2020/5/11 13:15
 */
@SuppressWarnings({"rawtypes"})
public abstract class AbstractFuture<V> implements Future<V> {


    @Override
    public V get() throws InterruptedException, ExecutionException {
        await();
        Throwable t = cause();
        if (t == null) {
            return getNow();
        }
        if (t instanceof CancellationException) {
            throw (CancellationException) t;
        }
        throw new ExecutionException(t);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (await(timeout, unit) != null) {
            Throwable t = cause();
            if (t == null) {
                return getNow();
            }
            if (t instanceof CancellationException) {
                throw (CancellationException) t;
            }
            throw new ExecutionException(t);
        }
        throw new TimeoutException();
    }

}
