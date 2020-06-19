package com.thinkerwolf.gamer.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "gamer")
@Component
public class GamerProperties {
    /**
     * Gamer Application id
     */
    private int id = 1;
    /**
     * Gamer Application type
     */
    private String type;
    /**
     * Servlet config file position
     */
    private String configFile = "conf.yml";
    /**
     * Remoting name
     */
    private String remoting = "netty";
    /**
     * Servlet bootstrap type
     */
    private String servletBoot = "netty";

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
