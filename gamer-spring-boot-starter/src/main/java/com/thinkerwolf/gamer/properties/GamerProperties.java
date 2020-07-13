package com.thinkerwolf.gamer.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@ConfigurationProperties(prefix = "gamer")
@Component
public class GamerProperties {
    /**
     * Gamer Application id
     */
    private String id = "";
    /**
     * Gamer Application type
     */
    private String type;
    /**
     * Servlet config file position
     */
    private String configFile;

    /**
     * Remoting name
     */
    private String remoting = "netty";
    /**
     * Servlet bootstrap type
     */
    private String servletBoot = "netty";
    /**
     * Conf. If <strong><i>configFile</i></strong> is not assigned, it will use this to initialize the <strong>ServletBootstrap</strong>
     */
    private Map<String, Object> conf;


    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void setRemoting(String remoting) {
        this.remoting = remoting;
    }

    public String getRemoting() {
        return remoting;
    }

    public String getServletBoot() {
        return servletBoot;
    }

    public void setServletBoot(String servletBoot) {
        this.servletBoot = servletBoot;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getConf() {
        return conf;
    }

    public void setConf(Map<String, Object> conf) {
        this.conf = conf;
    }
}
