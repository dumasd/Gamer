package com.thinkerwolf.gamer.common.util;

import com.thinkerwolf.gamer.common.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 资源工具类
 *
 * @author wukai
 */
public class ResourceUtils {

    public static final Set<URL> classPathURLs = new HashSet<>();

    public static final String CLASS_PATH_LOCATION;

    private static ThreadLocal<byte[]> threadBuffer = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {

            return new byte[1024 * 4];
        }
    };

    static {
        CLASS_PATH_LOCATION = ClassUtils.getDefaultClassLoader().getResource("").getPath();
        try {
            Enumeration<URL> classpathUrls = ClassUtils.getDefaultClassLoader().getResources("");
            while (classpathUrls.hasMoreElements()) {
                classPathURLs.add(classpathUrls.nextElement());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取指定classpath目录下所有文件的路径
     *
     * @param rootDir classpath下某一级目录
     * @param suffix  后缀
     * @return
     */
    public static Set<String> findClasspathFilePaths(String rootDir, String suffix) {
        Set<String> paths = new LinkedHashSet<>();
        suffix = StringUtils.isEmpty(suffix) ? "" : "." + suffix;
        try {
            Enumeration<URL> en = ClassUtils.getDefaultClassLoader().getResources(rootDir);
            while (en.hasMoreElements()) {
                URL url = en.nextElement();
                int index = 0;
                for (URL u : classPathURLs) {
                    if (url.getPath().startsWith(u.getPath())) {
                        if (u.getPath().startsWith("/")) {
                            index = u.getPath().length() - 1;
                        } else {
                            index = u.getPath().length();
                        }
                        break;
                    }
                }
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {

                    for (String p : findFilePaths(url.getPath())) {
                        if (p.endsWith(suffix)) {
                            paths.add(p.substring(index));
                        }
                    }

                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                    try {
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> jars = jar.entries();
                        while (jars.hasMoreElements()) {
                            JarEntry jarEntry = jars.nextElement();
                            if (!jarEntry.isDirectory() && jarEntry.getName().endsWith(suffix)) {
                                paths.add(jarEntry.getName());
                            }
                        }
                    } catch (IOException e) {
                        //
                    }

                } else if ("class".equals(protocol)) {
                    if (url.getPath().endsWith(suffix)) {
                        paths.add(url.getPath());
                    }
                }
            }
        } catch (IOException e) {
        }
        return paths;
    }

    /**
     * 获取目录及其子目录下所有文件名称
     *
     * @param rootPath
     * @return
     * @see ResourceUtils#findFilePaths(File)
     */
    public static Set<String> findFilePaths(String rootPath) {
        return findFilePaths(new File(rootPath));
    }

    /**
     * 获取目录及其子目录下所有文件名称
     *
     * @param rootFile
     * @return
     */
    public static Set<String> findFilePaths(File rootFile) {
        Set<String> paths = new HashSet<>();
        findPathsByFile(rootFile, paths);
        return paths;
    }

    /**
     * 获取目录及其子目录下所有文件
     *
     * @param rootPath
     * @return
     */
    public static Set<File> findFiles(String rootPath) {
        return findFiles(new File(rootPath));
    }

    public static Set<File> findFiles(File rootFile) {
        Set<File> files = new HashSet<>();
        findFilesByFile(rootFile, files);
        return files;
    }

    private static void findPathsByFile(File rootFile, Set<String> paths) {
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isFile()) {
            paths.add(rootFile.getPath().replace(File.separatorChar, '/'));
        } else if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            if (files != null) {
                for (File f : files) {
                    findPathsByFile(f, paths);
                }
            }
        }
    }

    private static void findFilesByFile(File rootFile, Set<File> files) {
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isFile()) {
            files.add(rootFile);
        } else if (rootFile.isDirectory()) {
            File[] fs = rootFile.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    findFilesByFile(f, files);
                }
            }
        }
    }

    public static byte[] toByteArray(Resource resource) throws IOException {
        ByteArrayOutputStream baos = null;
        InputStream inputStream = null;
        try {
            byte[] buffer = threadBuffer.get();
            inputStream = resource.getInputStream();
            int estimate = inputStream.available();
            baos = new ByteArrayOutputStream(estimate);
            int n;
            while (-1 != (n = inputStream.read(buffer))) {
                baos.write(buffer, 0, n);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {

            }
        }

    }

}
