package com.thinkerwolf.gamer.remoting;

/**
 * Server端
 */
public interface Server extends Endpoint {

    int DEFAULT_WORKER_THREADS = Math.min(Runtime.getRuntime().availableProcessors(), 32);

    void startup() throws Exception;

}
