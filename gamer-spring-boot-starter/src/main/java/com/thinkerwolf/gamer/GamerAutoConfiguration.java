package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.servlet.ServletBootstrap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(ServletBootstrap.class)
@Import({GamerServerConfiguration.class})
public class GamerAutoConfiguration {


}
