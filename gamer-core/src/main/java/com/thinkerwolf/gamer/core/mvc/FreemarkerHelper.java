package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.*;

public class FreemarkerHelper {

    private static final Logger LOG = InternalLoggerFactory.getLogger(FreemarkerHelper.class);

    private static final String DEFAULT_TEMPLATE_LOCATION = "templates";

    private static Configuration configuration;

    public static void init(ServletConfig servletConfig) throws Exception {
        configuration = new Configuration(Configuration.VERSION_2_3_20);
        configuration.setSetting(Configuration.LOCALIZED_LOOKUP_KEY, "false");

        List<TemplateLoader> loaderList = new ArrayList<>();
        loaderList.add(new MyClassTemplateLoader(FreemarkerHelper.class, DEFAULT_TEMPLATE_LOCATION));
        if (servletConfig != null) {
            String[] ls = StringUtils.split(servletConfig.getInitParam(ServletConfig.TEMPLATES_LOCATION), ';');
            if (ls != null) {
                for (String l : ls) {
                    if (StringUtils.isBlank(l)) {
                        continue;
                    }
                    File file = new File(l);
                    if (file.exists() && file.isDirectory()) {
                        loaderList.add(new FileTemplateLoader(file));
                    } else {
                        loaderList.add(new MyClassTemplateLoader(FreemarkerHelper.class, l));
                    }
                }
            }
        }
        MultiTemplateLoader multiLoader = new MultiTemplateLoader(loaderList.toArray(new TemplateLoader[0]));
        configuration.setTemplateLoader(multiLoader);
    }


    public static byte[] getTemplateBytes(String file, Map<String, Object> data) {
        Template template;
        try {
            template = configuration.getTemplate(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        OutputStreamWriter writer = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            writer = new OutputStreamWriter(output);
            template.process(data, writer);
            return output.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }

    }


    static class MyClassTemplateLoader extends freemarker.cache.URLTemplateLoader {
        private Class loaderClass;
        private String path;

        public MyClassTemplateLoader(Class loaderClass, String path) {
            setFields(loaderClass, path);
        }

        @Override
        protected URL getURL(String name) {
            return loaderClass.getClassLoader().getResource(path + name);
        }

        private void setFields(Class loaderClass, String path) {
            if (loaderClass == null) {
                throw new IllegalArgumentException("loaderClass == null");
            }
            if (path == null) {
                throw new IllegalArgumentException("path == null");
            }
            this.loaderClass = loaderClass;
            this.path = canonicalizePrefix(path);
        }
    }

}
