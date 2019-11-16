package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.io.Resource;
import com.thinkerwolf.gamer.common.io.Resources;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.core.mvc.model.ResourceModel;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.util.CompressUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 静态资源管理器
 *
 * @author wukai
 */
public class ResourceManager {

    private static final Logger LOG = InternalLoggerFactory.getLogger(ResourceManager.class);

    private static final String DEFAULT_RESOURCE_STATIC = "static";

    private static final String DEFAULT_RESOURCE_PUBLIC = "public";

    private List<String> resourceLocations = new LinkedList<>();

    private Map<String, ResourceModel> modelCache = new HashMap<>();

    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    public void init(ServletConfig servletConfig) {
        resourceLocations.add(ResourceUtils.CLASS_PATH_LOCATION + DEFAULT_RESOURCE_PUBLIC);
        resourceLocations.add(ResourceUtils.CLASS_PATH_LOCATION + DEFAULT_RESOURCE_STATIC);

        String location = servletConfig.getInitParam(ServletConfig.RESOURCE_LOCATION);
        if (location != null && location.length() > 0) {
            String[] ls = location.split(";");
            for (String l : ls) {
                if (l != null && l.length() > 0) {
                    File file = new File(ResourceUtils.CLASS_PATH_LOCATION + l);
                    if (file.exists()) {
                        resourceLocations.add(file.getAbsolutePath());
                    } else {
                        LOG.info("Resource location not found : " + l);
                    }
                }
            }
        }
    }

    public ResourceModel getResource(String path) throws IOException {
        path = path.replaceAll("\\\\", "/");
        readWriteLock.readLock().lock();
        try {
            ResourceModel resourceModel = modelCache.get(path);
            if (resourceModel != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find resource cache : " + path);
                }
                return resourceModel;
            }
        } finally {
            readWriteLock.readLock().unlock();
        }

        File file = null;
        for (String location : resourceLocations) {
            file = new File(location + File.separator + path);
            if (file.exists()) {
                break;
            }
        }
        if (file == null || !file.exists()) {
            return null;
        }

        int idxs = path.lastIndexOf("/");
        int idxe = path.lastIndexOf(".");
        String name = path.substring(idxs + 1, idxe);
        String extension = path.substring(idxe + 1);

        readWriteLock.writeLock().lock();
        try {
            Resource resource = Resources.getResource("file:" + file.getAbsolutePath());
            byte[] data = ResourceUtils.toByteArray(resource);
            ResourceModel resourceModel = new ResourceModel(data, name, extension);
            modelCache.put(path, resourceModel);
            return resourceModel;
        } catch (Exception e) {
            throw e;
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }


    public ResourceModel getResource(String path, String encoding) throws IOException {
        String key = path + "-" + encoding;
        readWriteLock.readLock().lock();
        try {
            ResourceModel resourceModel = modelCache.get(key);
            if (resourceModel != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Find compressed resource cache : " + path);
                }
                return resourceModel;
            }
        } finally {
            readWriteLock.readLock().unlock();
        }

        ResourceModel resourceModel = getResource(path);
        if (resourceModel == null) {
            return null;
        }

        if (resourceModel.compress()) {
            return resourceModel;
        }

        readWriteLock.writeLock().lock();
        try {
            if (!modelCache.containsKey(key)) {
                byte[] data = resourceModel.getData();
                boolean compressed = false;
                try {
                    byte[] compressData = CompressUtil.compress(data, encoding);
                    compressed = !Arrays.equals(compressData, data);
                    data = compressData;
                } catch (Exception e) {
                    LOG.warn("Compress data error, don't use compress", e);
                }

                ResourceModel compressedModel;
                if (compressed) {
                    compressedModel = new ResourceModel(data, resourceModel.file(), resourceModel.getExtension(), encoding);
                } else {
                    compressedModel = resourceModel;
                }
                modelCache.put(key, compressedModel);
            }
            return modelCache.get(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

}
