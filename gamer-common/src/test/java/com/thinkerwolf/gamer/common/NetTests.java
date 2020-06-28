package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.NetUtils;
import org.junit.Test;

import java.net.NetworkInterface;

public class NetTests {

    @Test
    public void ipAddress() {
        NetworkInterface ni = NetUtils.findNetworkInterface();
        System.out.println(ni);

        System.out.println(NetUtils.getLocalAddress().getHostAddress());
    }

}
