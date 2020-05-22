package com.thinkerwolf.gamer.registry;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.common.URL;

/**
 * 注册中心工厂
 *
 * @author wukai
 * @date 2020/5/14 15:41
 */
@SPI("zookeeper")
public interface RegistryFactory {
    // com.thinkerwolf.gamer.registry.RegistryFactory

    /**
     * 创建注册中心
     *
     * @param url url
     * @return Registry
     * @throws Exception exception
     */
    Registry create(URL url) throws Exception;

}
