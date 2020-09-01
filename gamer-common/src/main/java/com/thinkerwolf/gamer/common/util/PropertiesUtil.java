package com.thinkerwolf.gamer.common.util;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ResourceBundle util
 *
 * @author wukai
 */
public final class PropertiesUtil {

    private static final Logger LOG = InternalLoggerFactory.getLogger(PropertiesUtil.class);
    private static final String PACKAGE = "package";
    private static final Map<String, ResourceBundle> class2Bundles = new ConcurrentHashMap<>();
    private static Locale DEFAULT_LOCALE;

    static {
        try {
            Method method = Locale.class.getDeclaredMethod("initDefault");
            method.setAccessible(true);
            DEFAULT_LOCALE = (Locale) method.invoke(null);
        } catch (Exception e) {
            LOG.warn("Locale.initDefault()", e);
            DEFAULT_LOCALE = Locale.CHINA;
        }
    }

    public static String getString(Class<?> clazz, String key, Locale locale) {
        ResourceBundle bundle = getBundle(clazz, locale);
        if (bundle == null || !bundle.containsKey(key)) {
            return "";
        }
        return bundle.getString(key);
    }

    public static String getString(Class<?> clazz, String key) {
        return getString(clazz, key, DEFAULT_LOCALE);
    }

    public static Integer getInteger(Class<?> clazz, String key) {
        return getInteger(clazz, key, DEFAULT_LOCALE);
    }

    public static Long getLong(Class<?> clazz, String key) {
        return getLong(clazz, key, DEFAULT_LOCALE);
    }

    public static Double getDouble(Class<?> clazz, String key) {
        return getDouble(clazz, key, DEFAULT_LOCALE);
    }

    public static Boolean getBoolean(Class<?> clazz, String key) {
        return getBoolean(clazz, key, DEFAULT_LOCALE);
    }


    public static Integer getInteger(Class<?> clazz, String key, Locale locale) {
        String t = getString(clazz, key, locale);
        return Integer.parseInt(t);
    }

    public static Long getLong(Class<?> clazz, String key, Locale locale) {
        String t = getString(clazz, key, locale);
        return Long.parseLong(t);
    }

    public static Double getDouble(Class<?> clazz, String key, Locale locale) {
        String t = getString(clazz, key, locale);
        return Double.parseDouble(t);
    }

    public static Boolean getBoolean(Class<?> clazz, String key, Locale locale) {
        String t = getString(clazz, key, locale);
        return Boolean.parseBoolean(t);
    }

    /**
     * @param clazz
     * @param locale
     * @return
     */
    public static ResourceBundle getBundle(Class<?> clazz, Locale locale) {
        String key = classBundleKey(clazz, locale);
        ResourceBundle bundle;
        bundle = class2Bundles.get(key);
        if (bundle == null) {
            bundle = createBundle(clazz, locale);
        }
        if (bundle != null) {
            class2Bundles.put(key, bundle);
        }
        return bundle;
    }


    private static ResourceBundle createBundle(Class<?> clazz, Locale locale) {
        ResourceBundle bundle = null;
        String name = clazz.getName();
        while (bundle == null) {
            int idx = name.lastIndexOf('.');
            String baseName;
            if (idx > 0) {
                name = name.substring(0, name.lastIndexOf('.'));
                baseName = name + "." + PACKAGE;
            } else {
                if (name.length() > 0) {
                    baseName = name + "." + PACKAGE;
                    name = "";
                } else {
                    baseName = PACKAGE;
                }
            }
            try {
                bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
            } catch (Exception ignored) {
                bundle = null;
            }
            if (name.length() <= 0) {
                break;
            }
        }
        return bundle;
    }

    private static String classBundleKey(Class<?> clazz, Locale locale) {
        return clazz.getName() + "_" + locale.toString();
    }

}
