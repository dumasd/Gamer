package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.conf.Conf;

import java.util.List;

@SPI("netty")
public interface ServletBootstrapFactory {

    /**
     * 创建ServletBootstrap
     *
     * @param configFile 配置文件位置
     * @return ServletBootstrap
     */
    ServletBootstrap create(String configFile);

    /**
     * 创建ServletBootstrap
     *
     * @param urls          连接地址
     * @param servletConfig servletConfig
     * @return ServletBootstrap
     */
    ServletBootstrap create(List<URL> urls, ServletConfig servletConfig);

}
