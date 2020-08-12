package com.thinkerwolf.gamer.common;

import java.util.regex.Pattern;

/**
 * 常量列表
 *
 * @author wukai
 */
public final class Constants {
    /**
     * Classpath下默认配置文件
     */
    public static final String DEFAULT_CONFIG_FILE_YML = "conf.yml";
    /**
     * Classpath下默认配置文件
     */
    public static final String DEFAULT_CONFIG_FILE_YAML = "conf.yaml";

    /**
     * LOG配置文件
     */
    public static final String GAMER_LOG_CONFIG_FILE = "gamer.log.configFile";


    /**
     * <h2>应用ID:环境变量key</h2>
     *
     * <p>
     * 获取应用ID优先级
     * <ul>
     * <li><strong>环境变量:GAMER_MY_ID</strong></li>
     * <li>虚拟机参数:-Dgamer.my.id</li>
     * <li>随机生成的8位字符串</li>
     * </ul>
     * </p>
     *
     * @see Constants#JVM_GAMER_MY_ID
     */
    public static final String ENV_GAMER_MY_ID = "GAMER_MY_ID";
    /**
     * <h2>应用ID:虚拟机参数key</h2>
     * <p>
     * 获取应用ID优先级
     * <ul>
     * <li>环境变量:GAMER_MY_ID</li>
     * <li><strong>虚拟机参数:-Dgamer.my.id</strong></li>
     * <li>随机生成的8位字符串</li>
     * </ul>
     * </p>
     *
     * @see Constants#ENV_GAMER_MY_ID
     */
    public static final String JVM_GAMER_MY_ID = "gamer.my.id";

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
     * 优先选择的网络接口名称
     */
    public static final String GAMER_PREFERRED_NETWORK_INTERFACE = "gamer.network.interface.preferred";
    /**
     * KeepAlive time
     */
    public static final String EXECUTOR_KEEP_ALIVE_TIME = "executor.keepAlive.time";

    /**
     * Enabled
     */
    public static final String SSL_ENABLED = "enabled";
    /**
     * KeyStore 文件位置
     */
    public static final String SSL_KEYSTORE_FILE = "keystoreFile";
    /**
     * KeyStore 密码
     */
    public static final String SSL_KEYSTORE_PASS = "keystorePass";
    /**
     * TrustStore 文件位置
     */
    public static final String SSL_TRUSTSTORE_FILE = "truststoreFile";
    /**
     * TrustStore 密码
     */
    public static final String SSL_TRUSTSTORE_PASS = "truststorePass";

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
