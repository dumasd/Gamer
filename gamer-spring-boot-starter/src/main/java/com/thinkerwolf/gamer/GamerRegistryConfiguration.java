package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.common.ServiceLoader;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.properties.GamerRegistryProperties;
import com.thinkerwolf.gamer.registry.Registry;
import com.thinkerwolf.gamer.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({GamerRegistryProperties.class})
@ConditionalOnProperty(prefix = "gamer.registry", name = "enabled", matchIfMissing = true)
public class GamerRegistryConfiguration implements ApplicationContextAware {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }

    @Bean
    public Registry registry(GamerRegistryProperties registryProperties) throws Exception {
        URL url = URL.parse(registryProperties.getClient().getServiceUrl());
        RegistryFactory factory = ServiceLoader.getService(url.getProtocol(), RegistryFactory.class);
        return factory.create(url);
    }


}
