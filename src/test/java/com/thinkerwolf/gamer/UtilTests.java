package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.netty.NettyConfig;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public class UtilTests {

    @Test
    public void classUtilTest() {
        System.out.println(ClassUtils.castTo(3, Integer.class));
        System.out.println(ClassUtils.castTo(3, Double.class));

        try {
           Method method = NettyConfig.class.getMethod("setBossThreads", int.class);
            Annotation[][] annotatons = method.getParameterAnnotations();
            System.out.println(Arrays.deepToString(annotatons));

            System.err.println((int.class == Integer.TYPE));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

}
