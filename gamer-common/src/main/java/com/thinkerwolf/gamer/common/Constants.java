package com.thinkerwolf.gamer.common;

import java.util.regex.Pattern;

/**
 * 常量列表
 *
 * @author wukai
 */
public final class Constants {
    /** Classpath下默认配置文件 */
    public static final String DEFAULT_CONFIG_FILE_YML = "conf.yml";
    /** Classpath下默认配置文件 */
    public static final String DEFAULT_CONFIG_FILE_YAML = "conf.yaml";

    /** LOG配置文件 */
    public static final String GAMER_LOG_CONFIG_FILE = "gamer.log.configFile";

    /**
     *
     *
     * <h2>应用ID:环境变量key</h2>
     *
     * <p>获取应用ID优先级
     *
     * <ul>
     *   <li><strong>环境变量:GAMER_MY_ID</strong>
     *   <li>虚拟机参数:-Dgamer.my.id
     *   <li>随机生成的8位字符串
     * </ul>
     *
     * @see Constants#JVM_GAMER_MY_ID
     */
    public static final String ENV_GAMER_MY_ID = "GAMER_MY_ID";
    /**
     *
     *
     * <h2>应用ID:虚拟机参数key</h2>
     *
     * <p>获取应用ID优先级
     *
     * <ul>
     *   <li>环境变量:GAMER_MY_ID
     *   <li><strong>虚拟机参数:-Dgamer.my.id</strong>
     *   <li>随机生成的8位字符串
     * </ul>
     *
     * @see Constants#ENV_GAMER_MY_ID
     */
    public static final String JVM_GAMER_MY_ID = "gamer.my.id";

    /** LOG配置等级 INFO,CONSOLE */
    public static final String GAMER_LOG_PROP = "gamer.root.logger";

    /** 日志路径 */
    public static final String GAMER_LOG_DIR = "gamer.log.dir";

    /** 框架名称 */
    public static final String FRAMEWORK_NAME = "Thinkerwolf-Gamer";
    /** 框架版本号 */
    public static final String VERSION = "1.0.0";
    /** 名称版本 */
    public static final String FRAMEWORK_NAME_VERSION =
            String.format("%s/%s", FRAMEWORK_NAME, VERSION);

    /** 优先选择的网络接口名称 */
    public static final String GAMER_PREFERRED_NETWORK_INTERFACE =
            "gamer.network.interface.preferred";
    /** KeepAlive time */
    public static final String EXECUTOR_KEEP_ALIVE_TIME = "executor.keepAlive.time";

    /** Enabled */
    public static final String SSL_ENABLED = "enabled";
    /** KeyStore 文件位置 */
    public static final String SSL_KEYSTORE_FILE = "keystoreFile";
    /** KeyStore 密码 */
    public static final String SSL_KEYSTORE_PASS = "keystorePass";
    /** TrustStore 文件位置 */
    public static final String SSL_TRUSTSTORE_FILE = "truststoreFile";
    /** TrustStore 密码 */
    public static final String SSL_TRUSTSTORE_PASS = "truststorePass";
    /** */
    public static final String LOADBALANCE_KEY = "loadbalanceKey";

    /** ; */
    public static final Pattern SEMICOLON_SPLIT_PATTERN = Pattern.compile("\\s*[;]+\\s*");
    /** / */
    public static final Pattern PATH_SPLIT_PATTERN = Pattern.compile("\\s*[/]+\\s*");
    /** : */
    public static final Pattern COLON_SPLIT_PATTERN = Pattern.compile("\\s*[:]+\\s*");
    /** , */
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String PROTOCOL = "protocol";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PARAMETERS = "parameters";

    public static final String NET = "net";
    public static final String LISTENERS = "listeners";
    public static final String ENABLED = "enabled";
    public static final String INIT_PARAMS = "initParams";
    public static final String SERVLET = "servlet";
    public static final String SERVLET_NAME = "servletName";
    public static final String SERVLET_CLASS = "servletClass";

    public static final String RPC_USE_LOCAL = "rpcUseLocal";
    public static final String RPC_HOST = "rpcHost";
    public static final String SERVER = "server";
    public static final String BOSS_THREADS = "bossThreads";
    public static final String WORKER_THREADS = "workerThreads";
    public static final String CORE_THREADS = "coreThreads";
    public static final String MAX_THREADS = "maxThreads";
    public static final String COUNT_PER_CHANNEL = "countPerChannel";
    public static final String OPTIONS = "options";
    public static final String CHILD_OPTIONS = "childOptions";
    public static final String CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String SESSION_TIMEOUT = "sessionTimeout";
    public static final String BACKUP = "backup";
    public static final String NODE_EPHEMERAL = "nodeEphemeral";
    public static final String NODE_NAME = "nodeName";
    public static final String REQUEST_TIMEOUT = "requestTimeout";
    public static final String RPC_CLIENT_NUM = "rpcClientNum";
    public static final String RETRY = "retry";
    public static final String RETRY_MILLIS = "retryMillis";
    public static final String CHANNEL_HANDLERS = "channelHandlers";
    public static final String SERVLET_CONFIG = "servletConfig";
    public static final String EXEC_GROUP_NAME = "execGroupName";

    public static final int DEFAULT_TCP_PORT = 8777;
    public static final int DEFAULT_HTTP_PORT = 80;
    public static final int DEFAULT_CORE_THREADS = 10;
    public static final int DEFAULT_MAX_THREADS = 10;
    public static final int DEFAULT_COUNT_PERCHANNEL = 10;
    public static final String DEFAULT_SERVER = "netty";
}
