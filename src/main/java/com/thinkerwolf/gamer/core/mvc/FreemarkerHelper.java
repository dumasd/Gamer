package com.thinkerwolf.gamer.core.mvc;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.util.ResourceUtils;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;

public class FreemarkerHelper {

    private static final Logger LOG = InternalLoggerFactory.getLogger(FreemarkerHelper.class);

    private static final String DEFAULT_TEMPLATE_LOCATION = "templates";

    private static Map<String, Configuration> configurationMap = new LinkedHashMap<>();

    public static void init(ServletConfig servletConfig) throws Exception {
        File defaultFile = new File(ResourceUtils.CLASS_PATH_LOCATION + DEFAULT_TEMPLATE_LOCATION);
        if (defaultFile.exists()) {
            Configuration defaultConfiguration = new Configuration();
            defaultConfiguration.setDirectoryForTemplateLoading(defaultFile);
            configurationMap.put(defaultFile.getAbsolutePath(), defaultConfiguration);
        }

        String templatesLocation = servletConfig.getInitParam(ServletConfig.TEMPLATES_LOCATION);
        if (StringUtils.isNotBlank(templatesLocation)) {
            String[] ls = StringUtils.split(templatesLocation, ';');
            for (String l : ls) {
                if (StringUtils.isNotBlank(l)) {
                    File file = new File(ResourceUtils.CLASS_PATH_LOCATION + l.trim());
                    if (file.exists() && !file.equals(defaultFile)) {
                        Configuration configuration = new Configuration();
                        configuration.setDirectoryForTemplateLoading(file);
                        configurationMap.put(file.getAbsolutePath(), configuration);
                    }
                }
            }
        }
    }


    public static byte[] getTemplateBytes(String file, Map<String, Object> data) {
        Template template = null;
        for (Map.Entry<String, Configuration> en : configurationMap.entrySet()) {
            File f = new File(en.getKey() + File.separator + file);
            if (f.exists()) {
                try {
                    template = en.getValue().getTemplate(file);
                    if (template != null) {
                        break;
                    }
                } catch (Exception e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Error", e);
                    }
                }
            }
        }

        if (template != null) {
            OutputStreamWriter writer = null;
            try {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                writer = new OutputStreamWriter(output);
                template.process(data, writer);
                return output.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                }
            }
        }
        throw new RuntimeException("Freemarker template not found: " + file);
    }


}
