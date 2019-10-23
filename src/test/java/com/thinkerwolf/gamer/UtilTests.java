package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import org.junit.Test;

public class UtilTests {

    @Test
    public void classUtilTest() {
        System.out.println(ClassUtils.castTo(3, int.class));
        System.out.println(ClassUtils.castTo(3, double.class));

    }

}
