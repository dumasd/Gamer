package com.thinkerwolf.gamer.properties;

import static com.thinkerwolf.gamer.properties.GamerProperties.GamerConstants.GAMER_PREFIX;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = GAMER_PREFIX)
public class GamerProperties {

    private String configFile = null;

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public static class GamerConstants {
        public static final String GAMER_PREFIX = "spring.gamer";
    }

}
