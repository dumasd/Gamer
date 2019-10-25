package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.netty.NettyConfig;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Pattern;

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

    @Test
    public void serviceLoader() {
        InternalLoggerFactory.getLogger("ccc").error("start service loader test");
        for (int i = 0;i < 1000; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ServiceLoader.getService("default", ObjectFactory.class);
                }
            }).start();
        }

    }

    @Test
    public void parttern() {


        String s = "v6test/teca";

        String regex = "?test/*";
        regex = regex.replace("?", "[0-9a-z]");
        regex = regex.replace("*", "[0-9a-z]{0,}");
        System.out.println(regex);

        Pattern pattern = Pattern.compile(regex);
        System.out.println(pattern.matcher(s).matches());






    }

}
