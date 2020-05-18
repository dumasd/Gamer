package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.PropertiesUtil;
import org.junit.Test;

public class PropertiesUtilTests {

    @Test
    public void testBase() {
        String message = PropertiesUtil.getString(PropertiesUtilTests.class, "test");
        System.out.println(message);
    }

}
