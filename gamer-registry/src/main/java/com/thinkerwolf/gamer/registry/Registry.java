package com.thinkerwolf.gamer.registry;

/**
 * 注册中心
 *
 * @author wukai
 * @date 2020/5/14 15:41
 */
public interface Registry {

    /**
     * 往注册中心注册
     *
     * @param o
     */
    boolean register(Object o);

    /**
     * 从注册中心解除注册
     *
     * @param o
     */
    boolean unregister(Object o);




}
