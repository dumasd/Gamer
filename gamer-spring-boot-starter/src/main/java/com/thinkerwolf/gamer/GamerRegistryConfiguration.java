package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.properties.GamerRegistryProperties;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GamerRegistryProperties.class})
public class GamerRegistryConfiguration {

    private static final Logger LOG = InternalLoggerFactory.getLogger(GamerRegistryConfiguration.class);

    @Configuration
    @ConditionalOnProperty(prefix = "gamer.registry", name = "enabled", matchIfMissing = true)
    public static class RegistryConfiguration {
        @Bean
        public Registry registry(GamerRegistryProperties registryProperties) {
            try {
                URL url = URL.parse(registryProperties.getClient().getServiceUrl());
                RegistryFactory factory = ServiceLoader.getService(url.getProtocol(), RegistryFactory.class);
                return factory.create(url);
            } catch (Exception e) {
                LOG.error("Error when initialize servlet", e);
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new RuntimeException(e);
            }
        }
    }

}
