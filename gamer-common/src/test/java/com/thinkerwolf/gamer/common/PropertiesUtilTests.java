package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.PropertiesUtil;
import org.junit.Test;

import java.util.Arrays;

public class PropertiesUtilTests {

    @Test
    public void testBase() {
        String message = PropertiesUtil.getString(PropertiesUtilTests.class, "test");
        System.out.println(message);
    }

    @Test
    public void testCommon() {
        String[] ss = Constants.PATH_SPLIT_PATTERN.split("/spring/fdadf/jsk");
        System.out.println(Arrays.toString(ss));
    }

}
