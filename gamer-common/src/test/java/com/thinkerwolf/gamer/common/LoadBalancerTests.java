package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.balance.LoadBalancer;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LoadBalancerTests {
    @Test
    public void testConsistentHash() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(RandomStringUtils.randomAscii(RandomUtils.nextInt(10) + 5));
        }
        LoadBalancer balancer = ServiceLoader.getService("consistentHash", LoadBalancer.class);
        for (int i = 0; i < 1000; i++) {
            String key = RandomStringUtils.randomAscii(RandomUtils.nextInt(10) + 5);
            System.out.println(balancer.select(list, key, null));
        }
    }

}
