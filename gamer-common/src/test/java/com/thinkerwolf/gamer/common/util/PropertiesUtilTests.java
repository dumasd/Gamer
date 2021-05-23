package com.thinkerwolf.gamer.common.util;

import com.thinkerwolf.gamer.common.Constants;
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
