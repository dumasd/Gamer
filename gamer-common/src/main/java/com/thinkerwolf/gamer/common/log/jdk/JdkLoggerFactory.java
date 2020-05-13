package com.thinkerwolf.gamer.common.log.jdk;

import com.thinkerwolf.gamer.common.io.Resource;
import com.thinkerwolf.gamer.common.io.Resources;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import org.apache.commons.lang.StringUtils;

import java.util.logging.LogManager;

public class JdkLoggerFactory extends InternalLoggerFactory {

    private static final String DEFAULT_CONFIG_FILE_LOCATION = "classpath:logging.properties";

    private String configFileLocation;

    public JdkLoggerFactory() {
        this(DEFAULT_CONFIG_FILE_LOCATION);
    }

    public JdkLoggerFactory(String configFileLocation) {
        this.configFileLocation = configFileLocation;
        loadConfig();
    }

    private void loadConfig() {
        String location = configFileLocation;
        if (StringUtils.isEmpty(location)) {
            location = DEFAULT_CONFIG_FILE_LOCATION;
        }
        Resource resource = Resources.getResource(location);
        try {
            if (resource.getInputStream() != null) {
                LogManager.getLogManager().readConfiguration(resource.getInputStream());
                System.out.println("LOGGING:Load configuration from " + location);
            } else {
                System.out.println("LOGGING:Can't find file " + location + ", use default configuration.");
            }
        } catch (Exception e) {
            System.err.println("LOGGING:Load configuration error:");
            System.err.println(e);
        }
    }

    public Logger createLogger(String name) {
        return new JdkLogger(java.util.logging.Logger.getLogger(name));
    }

    public Logger createLogger(Class<?> clazz) {
        return new JdkLogger(java.util.logging.Logger.getLogger(clazz.getName()));
    }
}
