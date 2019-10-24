package com.thinkerwolf.gamer.common.util;


import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class ClassUtils {

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
        Object obj = null;
        if (clazz == Byte.TYPE || clazz == Byte.class) {
            obj = Byte.valueOf(s);
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
        }
        return (T) obj;
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


}
