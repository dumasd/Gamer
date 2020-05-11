package com.thinkerwolf.gamer.common.concurrent;

/**
 * @author wukai
 * @date 2020/5/11 13:16
 */
public interface FutureListener<F extends Future<?>> {

    void operationComplete(F future) throws Throwable;

}
