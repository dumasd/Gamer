package com.thinkerwolf.gamer.core.servlet;

import com.thinkerwolf.gamer.common.URL;

import java.io.Closeable;
import java.io.IOException;
/**
 * @author wukai
 * @since 2021-05-29
 */
public interface ServletServer extends Closeable {
    /** @return servlet */
    Servlet getServlet();

    /** @return */
    URL getURL();

    /** @throws Exception */
    void startup() throws Exception;

    /** @throws IOException */
    @Override
    void close() throws IOException;
}
