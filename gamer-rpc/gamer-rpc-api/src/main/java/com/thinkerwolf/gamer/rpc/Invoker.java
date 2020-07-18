package com.thinkerwolf.gamer.rpc;

/**
 * @author wukai
 * @date 2020/5/14 15:13
 */
public interface Invoker<T> {

    Result invoke(Object args) throws Throwable;

    /**
     * Invoker是否可用
     *
     * @return
     */
    default boolean isUsable() {
        return true;
    }

    default void destroy() {

    };

}
