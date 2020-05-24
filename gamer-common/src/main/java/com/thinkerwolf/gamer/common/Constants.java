package com.thinkerwolf.gamer.common;

import java.util.regex.Pattern;

/**
 * 常量列表
 *
 * @author wukai
 */
public final class Constants {

    /**
     * LOG配置文件
     */
    public static final String GAMER_LOG_CONFIG_FILE = "gamer.log.configFile";

    /**
     * LOG配置等级 INFO,CONSOLE
     */
    public static final String GAMER_LOG_PROP = "gamer.root.logger";

    /**
     * 日志路径
     */
    public static final String GAMER_LOG_DIR = "gamer.log.dir";


    /**
     * 框架名称
     */
    public static final String FRAMEWORK_NAME = "Thinkerwolf-Gamer";
    /**
     * 框架版本号
     */
    public static final String VERSION = "1.0.0";
    /**
     * 名称版本
     */
    public static final String FRAMEWORK_NAME_VERSION = String.format("%s/%s", FRAMEWORK_NAME, VERSION);

    /**
     * ;
     */
    public static final Pattern SEMICOLON_SPLIT_PATTERN = Pattern.compile("\\s*[;]+\\s*");
    /**
     * /
     */
    public static final Pattern PATH_SPLIT_PATTERN = Pattern.compile("\\s*[/]+\\s*");
    /**
     * :
     */
    public static final Pattern COLON_SPLIT_PATTERN = Pattern.compile("\\s*[:]+\\s*");
    /**
     * ,
     */
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

}
