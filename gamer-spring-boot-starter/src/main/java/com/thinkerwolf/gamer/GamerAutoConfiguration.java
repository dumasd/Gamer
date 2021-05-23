package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.SymbolConstants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.conf.yml.YmlConf;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrap;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrapFactory;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.properties.GamerProperties;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GamerProperties.class})
public class GamerAutoConfiguration {

    private static final Logger LOG = InternalLoggerFactory.getLogger(GamerAutoConfiguration.class);

    @Configuration
    @ConditionalOnMissingBean(value = ServletBootstrapBean.class)
    @AutoConfigureOrder(value = 1)
    protected static class ServerConfiguration implements ApplicationContextAware {
        private ApplicationContext applicationContext;

        @Bean
        public ServletBootstrap servletBootstrap(
                GamerProperties properties, @Autowired(required = false) Registry registry) {
            try {
                ServletBootstrapFactory factory =
                        ServiceLoader.getService(
                                properties.getServletBoot(), ServletBootstrapFactory.class);
                ServletBootstrap bootstrap;
                if (properties.getConfigFile() != null && !properties.getConfigFile().isEmpty()) {
                    bootstrap = factory.create(properties.getConfigFile());
                } else if (properties.getConf() != null) {
                    YmlConf conf = new YmlConf().load(properties.getConf());
                    //                    conf.setName(properties.getName());
                    bootstrap = factory.create(conf.getUrls(), conf.getServletConfig());
                } else {
                    bootstrap = factory.create(null);
                }
                bootstrap
                        .getServletConfig()
                        .getServletContext()
                        .setAttribute(
                                ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE,
                                applicationContext);
                if (registry != null) {
                    bootstrap
                            .getServletConfig()
                            .getServletContext()
                            .setAttribute(ServletContext.ROOT_REGISTRY, registry);
                }
                ServletBootstrapBean bootstrapBean = new ServletBootstrapBean(bootstrap);
                bootstrapBean.startup();
                return bootstrapBean;
            } catch (Exception e) {
                LOG.error("Initialize servlet", e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext context) throws BeansException {
            applicationContext = context;
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = "gamer.registry", name = "enabled")
    @AutoConfigureOrder
    protected static class RegistryConfiguration {
        @Bean
        public Registry registry(GamerProperties properties) {
            try {
                String address = properties.getRegistry().getAddress();
                if (StringUtils.isNotBlank(properties.getName())) {
                    if (address.endsWith(SymbolConstants.SLASH)) {
                        address += properties.getName();
                    } else {
                        address += (SymbolConstants.SLASH + properties.getName());
                    }
                }
                URL url = URL.parse(address);
                RegistryFactory factory =
                        ServiceLoader.getService(url.getProtocol(), RegistryFactory.class);
                return factory.create(url);
            } catch (Exception e) {
                LOG.error("Initialize registry", e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }
    }
}
