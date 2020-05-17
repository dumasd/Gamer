package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.netty.NettyServletBootstrap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(NettyServletBootstrap.class)
@Import({GamerServerConfiguration.class})
public class GamerAutoConfiguration {


}
