package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class ClassUtilTests {

    @Test
    public void testGetAnnotation() {
        Class clazz = TestAction.class;
        clazz.getDeclaredAnnotation(Service.class);

        Service s =  TestAction.class.getAnnotation(Service.class);
        System.out.println(s);
        s = ClassUtils.getAnnotation(clazz, Service.class);
        System.out.println(s);

        System.out.println(s.annotationType());


        System.out.println(ClassUtils.class.getCanonicalName());
        System.out.println(ClassUtils.class.getName());

        TestAction ta = new TestAction();
        for (Method method : ITestAction.class.getDeclaredMethods()) {
            System.out.println(method.getName());
            try {
                System.out.println(method.invoke(ta));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }



    }

    @Test
    public void testScan() {
        Set<Class> set= ClassUtils.scanClasses("org.springframework.stereotype");
        System.out.println(set);
    }

    @Test
    public void testURL() {
        String url = "default://wukai:123@127.0.0.1:8080/fdadt?useUnicode=true";
        URL u = URL.parse(url);
        System.out.println(u);

        System.out.println(url.getBytes().length);
    }



}
