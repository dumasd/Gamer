package com.thinkerwolf.gamer.netty.util;

import com.thinkerwolf.gamer.common.Constants;
import com.thinkerwolf.gamer.common.URL;
import com.thinkerwolf.gamer.common.util.CharsetUtil;
import com.thinkerwolf.gamer.netty.http.Http2HeadersAndDataFrames;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.stream.ChunkedInput;

import java.util.*;

public final class InternalHttpUtil {
    /**
     * Default max content length
     */
    public static final int DEFAULT_MAX_CONTENT_LENGTH = 65536;
    /**
     * Empty bytes
     */
    private static final byte[] EMPTY_BYTE = new byte[]{};
    /**
     * Http1 push request uri
     */
    public static final String LONG_HTTP = "longhttp";
    /**
     * Default Keep Alive timeout
     */
    public static final int DEFAULT_KEEP_ALIVE_TIMEOUT = 30000;

    public static boolean isLongHttp(String command) {
        return LONG_HTTP.equalsIgnoreCase(command);
    }

    public static List<byte[]> getRequestContent(HttpRequest request) {
        List<byte[]> result = new LinkedList<>();
        HttpMethod method = request.method();

        byte[] pathData;
        int i = request.uri().indexOf("?");
        if (i >= 0) {
            String getData = URL.decode(request.uri().substring(i + 1));
            pathData = getData.getBytes(CharsetUtil.UTF8);
        } else {
            pathData = EMPTY_BYTE;
        }
        result.add(pathData);

        // POST数据
        if (method.equals(HttpMethod.POST) && request instanceof FullHttpRequest) {
            ByteBuf buf = ((FullHttpRequest) request).content();
            byte[] postData = new byte[buf.readableBytes()];
            buf.readBytes(postData);
            result.add(postData);
        }
        return result;
    }

    public static List<byte[]> getRequestContent(Http2HeadersAndDataFrames frames) {
        List<byte[]> result = new LinkedList<>();
        Http2Headers headers = frames.headersFrame().headers();
        String path = headers.path().toString();
        byte[] pathData;
        int i = path.indexOf("?");
        if (i >= 0) {
            String getData = URL.decode(path.substring(i + 1));
            pathData = getData.getBytes(CharsetUtil.UTF8);
        } else {
            pathData = EMPTY_BYTE;
        }
        result.add(pathData);
        if (frames.dataFrame() != null) {
            ByteBuf buf = frames.dataFrame().content();
            byte[] postData = new byte[buf.readableBytes()];
            buf.readBytes(postData);
            result.add(postData);
        }
        return result;
    }

    public static String getCommand(HttpRequest request) {
        return getCommand(request.uri());
    }

    public static String getCommand(Http2HeadersAndDataFrames frames) {
        return getCommand(frames.headersFrame().headers().path());
    }

    public static String getCommand(CharSequence path) {
        String p = URL.decode(path.toString());
        if (p.startsWith("/")) {
            p = p.substring(1);
        }
        int i = p.indexOf("?");
        if (i < 0) {
            return p;
        } else {
            return p.substring(0, i);
        }
    }

    public static Map<String, Cookie> getCookies(HttpRequest request) {
        Map<String, Cookie> cookies = new HashMap<>();
        String value = request.headers().get(HttpHeaderNames.COOKIE);
        if (value != null) {
            Set<Cookie> set = ServerCookieDecoder.STRICT.decode(value);
            for (Cookie cookie : set) {
                Cookie c = new DefaultCookie(cookie.name(), cookie.value());
                c.setDomain(cookie.domain());
                c.setHttpOnly(cookie.isHttpOnly());
                c.setSecure(cookie.isSecure());
                c.setMaxAge(cookie.maxAge());
                c.setPath(cookie.path());
                c.setWrap(cookie.wrap());
                cookies.put(cookie.name(), c);
            }
        }
        return cookies;
    }

    public static Map<String, Cookie> getCookies(Http2HeadersFrame headersFrame) {
        Map<String, Cookie> cookies = new HashMap<>();
        CharSequence value = headersFrame.headers().get(HttpHeaderNames.COOKIE);
        if (value != null) {
            Set<Cookie> set = ServerCookieDecoder.STRICT.decode(value.toString());
            for (Cookie cookie : set) {
                Cookie c = new DefaultCookie(cookie.name(), cookie.value());
                c.setDomain(cookie.domain());
                c.setHttpOnly(cookie.isHttpOnly());
                c.setSecure(cookie.isSecure());
                c.setMaxAge(cookie.maxAge());
                c.setPath(cookie.path());
                c.setWrap(cookie.wrap());
                cookies.put(cookie.name(), c);
            }
        }
        return cookies;
    }

    public static Set<String> getAcceptEncodings(HttpRequest request) {
        String encoding = request.headers().get(HttpHeaderNames.ACCEPT_ENCODING);
        if (encoding != null) {
            encoding = encoding.trim();
        }
        if (encoding == null || encoding.length() == 0) {
            return Collections.emptySet();
        }
        String[] encodes = Constants.COMMA_SPLIT_PATTERN.split(encoding);
        Set<String> encodeSet = new LinkedHashSet<>();
        for (String encode : encodes) {
            if (encode != null && encode.trim().length() > 0) {
                encodeSet.add(encode);
            }
        }
        return encodeSet;
    }


    public static void chunkResponse(Channel channel, HttpRequest httpRequest, ChunkedInput chunkedInput) {
        HttpVersion version = httpRequest.protocolVersion();
        HttpResponse nettyResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);

        if (version == HttpVersion.HTTP_1_0) {
            nettyResponse.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        // 二进制流
        nettyResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_OCTET_STREAM);
        nettyResponse.headers().add(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
        nettyResponse.headers().add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);

        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);

        if (chunkedInput != null) {
            ChannelFuture writeFuture = channel.write(nettyResponse);
            if (httpRequest.method() != HttpMethod.HEAD) {
                channel.write(chunkedInput);
            }
            if (!keepAlive) {
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }

    }

    public static boolean isKeepAlive(Http2Headers headers) {
        return !headers.contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true) &&
                headers.contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);
    }

    public static String getWebSocketUrl(HttpRequest httpRequest) {
        return "ws://" + httpRequest.headers().get(HttpHeaderNames.HOST) + httpRequest.uri();
    }


}
