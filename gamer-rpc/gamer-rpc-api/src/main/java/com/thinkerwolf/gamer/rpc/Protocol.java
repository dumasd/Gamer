package com.thinkerwolf.gamer.rpc;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.common.URL;

/**
 * 协议
 *
 * @author wukai
 * @date 2020/5/14 10:30
 */
@SPI("tcp")
public interface Protocol {

    <T> Invoker<T> invoker(Class<T> interfaceClass, URL url);

}
