package com.thinkerwolf.gamer.properties;

import static com.thinkerwolf.gamer.properties.GamerProperties.GamerConstants.GAMER_PREFIX;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = GAMER_PREFIX)
@Component
public class GamerProperties {

    /**
     * 配置文件
     */
    private String configFile = null;
    /**
     * 远程框架名称
     */
    private String remoting = "netty";
    /**
     * Servlet启动器
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

    public static class GamerConstants {
        public static final String GAMER_PREFIX = "spring.gamer";
    }

}
