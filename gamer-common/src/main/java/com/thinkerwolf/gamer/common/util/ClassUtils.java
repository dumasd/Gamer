package com.thinkerwolf.gamer.common.util;


import com.thinkerwolf.gamer.common.io.Resources;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class ClassUtils {

    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    private static Map<Class<?>, Object> PRIMITIVE_DEFAULT_VALUE;
    private static Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER;

    static {
        PRIMITIVE_DEFAULT_VALUE = new HashMap<>();
        PRIMITIVE_WRAPPER = new HashMap<>();
        PRIMITIVE_DEFAULT_VALUE.put(Boolean.TYPE, Boolean.FALSE);
        PRIMITIVE_DEFAULT_VALUE.put(Byte.TYPE, (byte) 0);
        PRIMITIVE_DEFAULT_VALUE.put(Character.TYPE, (char) 0);
        PRIMITIVE_DEFAULT_VALUE.put(Short.TYPE, (short) 0);
        PRIMITIVE_DEFAULT_VALUE.put(Integer.TYPE, 0);
        PRIMITIVE_DEFAULT_VALUE.put(Long.TYPE, 0L);
        PRIMITIVE_DEFAULT_VALUE.put(Float.TYPE, 0F);
        PRIMITIVE_DEFAULT_VALUE.put(Double.TYPE, 0D);

        PRIMITIVE_WRAPPER.put(Boolean.TYPE, Boolean.class);
        PRIMITIVE_WRAPPER.put(Byte.TYPE, Byte.class);
        PRIMITIVE_WRAPPER.put(Character.TYPE, Character.class);
        PRIMITIVE_WRAPPER.put(Short.TYPE, Short.class);
        PRIMITIVE_WRAPPER.put(Integer.TYPE, Integer.class);
        PRIMITIVE_WRAPPER.put(Long.TYPE, Long.class);
        PRIMITIVE_WRAPPER.put(Float.TYPE, Float.class);
        PRIMITIVE_WRAPPER.put(Double.TYPE, Double.class);
    }


    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
        }
        if (cl == null) {
            cl = ClassUtils.class.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                }
            }
        }
        return cl;
    }

    public static Class<?> forName(String name) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object... args) {
        if (args.length == 0) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            try {
                return clazz.getConstructor(parameterTypes).newInstance(args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String name, Object... args) {
        return newInstance((Class<T>) forName(name), args);
    }


    public static <T> T castTo(String s, Class<T> clazz) {
        if (s == null) {
            return getDefaultValue(clazz);
        }
        try {
            Object obj = null;
            if (clazz == Byte.TYPE || clazz == Byte.class) {
                obj = Byte.parseByte(s);
            } else if (clazz == Character.TYPE || clazz == Character.class) {
                obj = (char) Integer.parseInt(s);
            } else if (clazz == Short.TYPE || clazz == Short.class) {
                obj = Short.valueOf(s);
            } else if (clazz == Integer.TYPE || clazz == Integer.class) {
                obj = Integer.valueOf(s);
            } else if (clazz == Long.TYPE || clazz == Long.class) {
                obj = Long.valueOf(s);
            } else if (clazz == Float.TYPE || clazz == Float.class) {
                obj = Float.valueOf(s);
            } else if (clazz == Double.TYPE || clazz == Double.class) {
                obj = Double.valueOf(s);
            } else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
                obj = Boolean.valueOf(s);
            } else if (CharSequence.class.isAssignableFrom(clazz)) {
                obj = s;
            }
            if (obj != null) {
                return (T) obj;
            }
        } catch (Throwable t) {

        }
        return getDefaultValue(clazz);
    }

    public static <T> T castTo(Object obj, Class<T> toClass) {
        if (obj == null) {
            return getDefaultValue(toClass);
        }
        Class<?> fromClass = obj.getClass();

        if (fromClass.getName().equals(toClass.getName())) {
            return (T) obj;
        } else if (toClass.isAssignableFrom(fromClass)) {
            return (T) obj;
        }

        if (fromClass == String.class) {
            return castTo((String) obj, toClass);
        }

        if (Number.class.isAssignableFrom(fromClass) && (Number.class.isAssignableFrom(toClass) || toClass.isPrimitive())) {
            return castTo(obj.toString(), toClass);
        }

        if (fromClass.isArray() && toClass.isArray()) {
            int len = Array.getLength(obj);
            Class<?> toComponentType = toClass.getComponentType();
            Object newArr = Array.newInstance(toComponentType, len);
            for (int i = 0; i < len; i++) {
                Array.set(newArr, i, castTo(Array.get(obj, i), toComponentType));
            }
            return (T) newArr;
        }
        return getDefaultValue(toClass);
    }

    public static <T> T getDefaultValue(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            return (T) PRIMITIVE_DEFAULT_VALUE.get(clazz);
        }
        return null;
    }

    public static <A extends Annotation> A getAnnotation(Class clazz, Class<A> annotationClass) {
        if (clazz == Object.class) {
            return null;
        }
        Annotation res = clazz.getAnnotation(annotationClass);
        if (res != null) {
            return (A) res;
        }
        res = getAnnotation(clazz.getSuperclass(), annotationClass);
        if (res != null) {
            return (A) res;
        }
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> ifac : interfaces) {
            res = getAnnotation(ifac, annotationClass);
            if (res != null) {
                return (A) res;
            }
        }
        return null;
    }

    public static Set<Class> scanClasses(String basePackage) {
        basePackage = basePackage.replace('.', '/');
        Set<String> set = ResourceUtils.findClasspathFilePaths(basePackage, "class");
        Set<Class> result = new LinkedHashSet<>();
        for (String s : set) {
            String classname = s.replaceAll("/", ".").replaceAll(".class", "");
            result.add(forName(classname));
        }
        return result;
    }

    public static String getDesc(final Class<?>[] cs) {
        if (cs.length == 0)
            return "";

        StringBuilder sb = new StringBuilder(64);
        for (Class<?> c : cs)
            sb.append(getDesc(c));
        return sb.toString();
    }

    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) ret.append(JVM_VOID);
            else if ("boolean".equals(t)) ret.append(JVM_BOOLEAN);
            else if ("byte".equals(t)) ret.append(JVM_BYTE);
            else if ("char".equals(t)) ret.append(JVM_CHAR);
            else if ("double".equals(t)) ret.append(JVM_DOUBLE);
            else if ("float".equals(t)) ret.append(JVM_FLOAT);
            else if ("int".equals(t)) ret.append(JVM_INT);
            else if ("long".equals(t)) ret.append(JVM_LONG);
            else if ("short".equals(t)) ret.append(JVM_SHORT);
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

    public static String getDesc(final Method m) {
        StringBuilder ret = new StringBuilder(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++)
            ret.append(getDesc(parameterTypes[i]));
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }
}
