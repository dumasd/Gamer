package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.ObjectFactory;
import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.util.ClassUtils;
import com.thinkerwolf.gamer.core.mvc.FreemarkerHelper;
import com.thinkerwolf.gamer.core.servlet.Servlet;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.core.servlet.StandardSessionIdGenerator;
import com.thinkerwolf.gamer.netty.NettyConfig;
import org.apache.commons.lang.time.StopWatch;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
        for (int i = 0; i < 1000; i++) {

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

        String classpath = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        System.err.println(classpath);


    }

    @Test
    public void template() throws Exception {
        Map<String, String> data = new HashMap<>();
        data.put(ServletConfig.TEMPLATES_LOCATION, "ccc");
        ServletConfig servletConfig = new ServletConfig() {
            @Override
            public String getServletName() {
                return null;
            }

            @Override
            public Class<? extends Servlet> servletClass() {
                return null;
            }

            @Override
            public String getInitParam(String key) {
                return data.get(key);
            }

            @Override
            public Collection<String> getInitParamNames() {
                return data.keySet();
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }
        };
        FreemarkerHelper.init(servletConfig);

        String templateName = "typeHandler.ftl";
        Class<?>[] classTypes = new Class<?>[]{Character.class};
        Map<String, Object> root = new HashMap<>();
        for (Class<?> type : classTypes) {
            root.put("packageName", "com.thinkerwolf.hantis.common.type");
            Class<?> classType = type;
            String suffix;
            if (type.isArray()) {
                classType = type.getComponentType();
                String simpleName = classType.getSimpleName();
                suffix = Character.toUpperCase(simpleName.charAt(0)) + simpleName.substring(1) + 's';
            } else {
                suffix = classType.getSimpleName();
            }
            root.put("type", suffix);
            if (classType.getName().startsWith("java.lang") || !classType.getName().contains(".")) {
                root.put("isNeedImport", false);
            } else {
                root.put("importClass", classType.getName());
                root.put("isNeedImport", true);
            }
            root.put("realType", type.getSimpleName());
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 10000; i++) {
            root.put("packageName", "ccvv" + i);
            byte[] bytes = FreemarkerHelper.getTemplateBytes("typeHandler.ftl", root);
//            System.out.println(new String(bytes));
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTime());
    }


    @Test
    public void sessionId() {
        byte b = 0x1e - 10;// = 36 - 10 = 26
        System.err.println(b >> 4);
        System.err.println('U' - 'A');
        System.err.println(((char) ('A' + b)));
        StandardSessionIdGenerator generator = new StandardSessionIdGenerator();
        generator.generateSessionId();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            System.err.println(generator.generateSessionId("yyy"));
        }
        System.err.println("Spend time : " + (System.currentTimeMillis() - t1));
    }


    @Test
    public void charTest() {
        char LF = 10;
        char CR = 13;

        StringBuilder sb = new StringBuilder();
        sb.append("acc:578")
                .append('\r')
                .append('\n')
                .append("zxc:78");

        String s = sb.toString();
        System.out.println(s);

        System.out.println(Arrays.toString(s.split("\n")));

    }

}
