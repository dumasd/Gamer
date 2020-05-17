package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.servlet.ServletContext;
import com.thinkerwolf.gamer.netty.NettyServletBootstrap;
import com.thinkerwolf.gamer.properties.GamerProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GamerProperties.class)
public class GamerServerConfiguration implements ApplicationContextAware {


    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public NettyServletBootstrap bootstrap(GamerProperties properties) throws Exception {
        NettyServletBootstrap bootstrap;
        if (properties.getConfigFile() != null) {
            bootstrap = new NettyServletBootstrap(properties.getConfigFile());
        } else {
            bootstrap = new NettyServletBootstrap();
        }
        bootstrap.getServletConfig().getServletContext().setAttribute(ServletContext.SPRING_APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
        bootstrap.startup();
        return bootstrap;
    }


}
