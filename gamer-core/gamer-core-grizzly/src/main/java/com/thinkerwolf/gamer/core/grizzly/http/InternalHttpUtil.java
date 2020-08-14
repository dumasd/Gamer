package com.thinkerwolf.gamer.core.grizzly.http;

import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.core.util.RequestUtil;
import com.thinkerwolf.gamer.remoting.Channel;
import org.apache.commons.lang.StringUtils;
import org.glassfish.grizzly.http.HttpContent;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.Method;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public final class InternalHttpUtil {

    public static HttpRequest createRequest(HttpContent httpContent, Channel channel, ServletConfig servletConfig) {
        HttpRequestPacket requestPacket = (HttpRequestPacket) httpContent.getHttpHeader();
        Method httpMethod = requestPacket.getMethod();
        byte[] content = parseContent(httpContent);
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotBlank(requestPacket.getQueryString())) {
            params.putAll(RequestUtil.parseParams(URL.decode(requestPacket.getQueryString())));
        }
        if ("PUT".equalsIgnoreCase(httpMethod.getMethodString())) {
            params.putAll(RequestUtil.parseParams(new String(content, StandardCharsets.UTF_8)));
        }
        int requestId = RequestUtil.getRequestId(params);
        return HttpRequest.builder()
                .setRequestId(requestId)
                .setCommand(getCommand(requestPacket))
                .setRequestPacket(requestPacket)
                .setServletConfig(servletConfig)
                .setCh(channel)
                .setAttributes(params)
                .setContent(content)
                .build();
    }

    public static byte[] parseContent(HttpContent httpContent) {
        if (httpContent.getContent() == null) {
            return new byte[]{};
        }
        int r = httpContent.getContent().remaining();
        byte[] bs = new byte[r];
        httpContent.getContent().get(bs);
        return bs;
    }

    public static String getCommand(HttpRequestPacket packet) {
        String uri = URL.decode(packet.getRequestURI());
        if (uri.isEmpty()) {
            return uri;
        }
        uri = uri.replaceAll("/", "");
        if (uri.isEmpty()) {
            return uri;
        }
        int idx = uri.indexOf("?");
        if (idx > 0) {
            return uri.substring(0, idx);
        }
        return uri;
    }


}
