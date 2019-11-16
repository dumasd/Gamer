package com.thinkerwolf.gamer.common.io;

import com.thinkerwolf.gamer.common.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 资源获取
 *
 * @author wukai
 */
public final class Resources {

    public static final String RREFFIX_CLASSPATH = "classpath:";
    public static final String RREFFIX_FILE = "file:";
    public static final String RREFFIX_HTTP = "http:";
    public static final String RREFFIX_HTTPS = "https:";
    public static final String USER_DIR = System.getProperty("user.dir");
    
    private Resources() {
    }

    public static Resource[] getResources(String[] paths) {
        Resource[] resources = new Resource[paths.length];
        for (int i = 0; i < paths.length; i++) {
            resources[i] = getResource(paths[i]);
        }
        return resources;
    }

    public static Resource[] getResources(String path) {
        path = path.replace(File.separatorChar, '/');
        if (path.startsWith(RREFFIX_CLASSPATH)) {
            return getClasspathResources(path);
        } else if (path.startsWith(RREFFIX_FILE)) {
            return getFileSystemResources(path);
        } else if (path.startsWith(RREFFIX_HTTP) || path.startsWith(RREFFIX_HTTPS)) {
            try {
                Resource[] resources = new Resource[1];
                resources[0] = new URLResource(new URL(path));
                return resources;
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            return getFileSystemResources(path);
        }
    }

    public static Resource[] getClasspathResources(String rootPath) {
        rootPath = rootPath.replace(File.separatorChar, '/');
        if (rootPath.startsWith(RREFFIX_CLASSPATH)) {
            rootPath = getRealPath(RREFFIX_CLASSPATH, rootPath);
        }
        String rootDir = getRootDir(rootPath);
        Pattern p = Pattern.compile(rootPath.replaceAll("\\*", ".*"));
        Set<String> set = ResourceUtils.findClasspathFilePaths(rootDir, "");
        for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            String s = iter.next();
            if (!p.matcher(s).matches()) {
                iter.remove();
            }
        }
        Resource[] resources = new Resource[set.size()];
        Iterator<String> iter = set.iterator();
        for (int i = 0; i < set.size(); i++) {
            resources[i] = new ClassPathResource(iter.next());
        }
        return resources;
    }

    public static Resource[] getFileSystemResources(String rootPath) {
        rootPath = rootPath.replace(File.separatorChar, '/');
        if (rootPath.startsWith(RREFFIX_FILE)) {
            rootPath = getRealPath(RREFFIX_FILE, rootPath);
        }
        if (rootPath.startsWith("/")) {
            rootPath = rootPath.substring(1);
        }
        String rootDir = getRootDir(rootPath);
        Pattern p = Pattern.compile(rootPath.replaceAll("\\*", ".*"));
        Set<String> set = ResourceUtils.findFilePaths(rootDir);
        for (Iterator<String> iter = set.iterator(); iter.hasNext(); ) {
            String s = iter.next();
            if (!p.matcher(s).matches()) {
                iter.remove();
            }
        }
        Resource[] resources = new Resource[set.size()];
        Iterator<String> iter = set.iterator();
        for (int i = 0; i < set.size(); i++) {
            resources[i] = new FileSystemResource(iter.next());
        }
        return resources;
    }

    public static String[] resolvePath(String path) throws IOException {
        List<String> paths = new ArrayList<>();
        if (path.startsWith(RREFFIX_CLASSPATH)) {
            String realPath = getRealPath(RREFFIX_CLASSPATH, path);
            String rootDir = getRootDir(realPath);
            Pattern p = Pattern.compile(realPath.replaceAll("\\*", ".*"));
            for (String s : ResourceUtils.findClasspathFilePaths(rootDir, "")) {
                if (p.matcher(s).matches()) {
                    paths.add(s);
                }
            }
        } else {

        }
        return paths.toArray(new String[paths.size()]);
    }

    public static Resource getResource(String path) {
        path = path.replace(File.separatorChar, '/');
        Resource resource = null;
        if (path.startsWith(RREFFIX_CLASSPATH)) {
            resource = new ClassPathResource(getRealPath(RREFFIX_CLASSPATH, path));
        } else if (path.startsWith(RREFFIX_FILE)) {
            resource = new FileSystemResource(getRealPath(RREFFIX_FILE, path));
        } else if (path.startsWith(RREFFIX_HTTP) || path.startsWith(RREFFIX_HTTPS)) {
            try {
                resource = new URLResource(new URL(path));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // no preffix
            resource = new FileSystemResource(new File(path));
        }
        return resource;
    }

    private static String getRealPath(String preffix, String path) {
        return path.substring(preffix.length());
    }

    /**
     * 获取路径的根目录 /root/
     *
     * @param path
     * @return
     */
    public static String getRootDir(String path) {
        String[] strs = path.split("/");
        StringBuilder rootDir = new StringBuilder();
        for (String s : strs) {
            if (s.contains("*")) {
                break;
            }
            rootDir.append(s);
            rootDir.append("/");
        }
        // if (rootDir.length() == 0) {
        // rootDir.append("/");
        // }
        return rootDir.toString();
    }

}
