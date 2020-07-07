package com.thinkerwolf.gamer.rpc.cluster;

import com.thinkerwolf.gamer.common.SPI;
import com.thinkerwolf.gamer.rpc.Invoker;

import java.util.List;

/**
 * 集群
 *
 * @author wukai
 * @date 2020/5/14 14:21
 */
@SPI("failfast")
public interface Cluster {

    <T> Invoker<T> combine(List<Invoker<T>> invokers);

}
