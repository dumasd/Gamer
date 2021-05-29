package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.common.URL;
/**
 * @author wukai
 * @since 2021-05-29
 */
@SPI("netty")
public interface ServletServerFactory {

    ServletServer newServer(Servlet servlet, URL url);
}
