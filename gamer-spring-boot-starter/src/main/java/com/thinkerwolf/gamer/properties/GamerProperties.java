package com.thinkerwolf.gamer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "gamer")
@Component
public class GamerProperties {
    /** Gamer Application name */
    private String name;
    /** Servlet config file position */
    private String configFile;
    /** Remoting name */
    private String remoting = "netty";
    /** Gamer registry info */
    private Registry registry;
    /**
     * Conf. If <strong><i>configFile</i></strong> is not assigned, it will use this to initialize
     * the <strong>ServletBootstrap</strong>
     */
    private Map<String, Object> conf;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Map<String, Object> getConf() {
        return conf;
    }

    public void setConf(Map<String, Object> conf) {
        this.conf = conf;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public static class Registry {
        private boolean enabled;
        private String address;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
