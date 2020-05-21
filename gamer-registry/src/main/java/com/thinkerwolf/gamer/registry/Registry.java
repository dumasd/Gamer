package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.SPI;
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

}
