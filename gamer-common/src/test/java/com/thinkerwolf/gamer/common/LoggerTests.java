package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.log.InternalLoggerFactory;
import com.thinkerwolf.gamer.common.log.Logger;
import com.thinkerwolf.gamer.common.log.commons.CommonsLoggerFactory;
import org.junit.Test;

public class LoggerTests {
    @Test
    public void testCommonsLogger() {
        InternalLoggerFactory.setDefaultLoggerFactory(new CommonsLoggerFactory());
        Logger logger = InternalLoggerFactory.getLogger(LoggerTests.class);
        logger.info("test {}, {}", 1, "321");
        logger.debug("test {}, {}", 1, "321");
        logger.warn("test {}, {}", 1, "321");
    }
}
