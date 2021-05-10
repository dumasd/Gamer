package com.thinkerwolf.gamer.common.balance;

import com.thinkerwolf.gamer.common.SPI;

import java.util.Collection;
import java.util.Map;

/**
 * 负载均衡
 *
 * @author wukai
 */
@SPI("random")
public interface LoadBalancer {
    // com.thinkerwolf.gamer.common.balance.LoadBalancer
    /**
     * select
     *
     * @param collection 待选择集合
     * @param searchKey        key
     * @param props      额外属性
     * @param <T>        选择类型
     * @return T
     */
    <T> T select(Collection<T> collection, String searchKey, Map<String, Object> props);

}
