package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.core.conf.yml.YmlConf;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrap;
import com.thinkerwolf.gamer.core.servlet.ServletBootstrapFactory;
import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.properties.GamerProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(GamerProperties.class)
public class GamerServerConfiguration {

    private static final Logger LOG = InternalLoggerFactory.getLogger(GamerServerConfiguration.class);

    @Configuration
    public static class ServletBootstrapConfiguration implements ApplicationContextAware {
        private ApplicationContext applicationContext;

        @Bean
        public ServletBootstrap servletBootstrap(GamerProperties properties) {
            try {
                ServletBootstrapFactory factory = ServiceLoader.getService(properties.getServletBoot(), ServletBootstrapFactory.class);
                ServletBootstrap bootstrap;
                if (properties.getConfigFile() != null && !properties.getConfigFile().isEmpty()) {
                    bootstrap = factory.create(properties.getConfigFile());
                } else if (properties.getConf() != null) {
                    YmlConf conf = new YmlConf().load(properties.getConf());
                    bootstrap = factory.create(conf.getUrls(), conf.getServletConfig());
                } else {
                    bootstrap = factory.create(null);
                }
                bootstrap.getServletConfig().getServletContext().setAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
                bootstrap.startup();
                return bootstrap;
            } catch (Exception e) {
                LOG.error("Error when initialize servlet", e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }
    }


}
