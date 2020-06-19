package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.URL;

import java.util.List;

/**
 * 注册中心
 *
 * @author wukai
 * @date 2020/5/14 15:41
 */
public interface Registry {
    /**
     * 注册中心地址
     *
     * @return
     */
    URL url();

    /**
     * 往注册中心注册
     *
     * @param
     */
    void register(URL url);

    /**
     * 从注册中心解除注册
     *
     * @param
     */
    void unregister(URL url);

    /**
     * 关闭注册中心
     */
    void close();


    List<URL> lookup(URL url);

    /**
     * 订阅节点
     *
     * @param url
     * @param listener
     */
    void subscribe(URL url, INotifyListener listener);

    /**
     * 取消订阅节点
     *
     * @param url
     * @param listener
     */
    void unsubscribe(URL url, INotifyListener listener);

    /**
     * 订阅Registry客户端状态
     *
     * @param listener
     */
    void subscribeState(IStateListener listener);

    /**
     * 取消订阅Registry客户端状态
     *
     * @param listener
     */
    void unsubscribeState(IStateListener listener);
}
