package com.thinkerwolf.gamer.common.concurrent;

/**
 * @author wukai
 * @date 2020/5/11 13:14
 */
public interface Promise<V> extends Future<V> {

    void setSuccess(V result);

    void setFailure(Throwable cause);

    Future<V> addListener(FutureListener<? extends Future<? super V>> listener);

    @SuppressWarnings("unchecked")
    Future<V> addListeners(FutureListener<? extends Future<? super V>>... listeners);

}
