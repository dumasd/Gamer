package com.thinkerwolf.gamer.core.conf;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;

import java.util.List;

public interface Conf<C extends Conf<C>> {

    C setServletConfig(ServletConfig servletConfig);

    ServletConfig getServletConfig();

    C setUrls(List<URL> urls);

    List<URL> getUrls();

    C setConfFile(String confFile);

    C load() throws Exception;

}
