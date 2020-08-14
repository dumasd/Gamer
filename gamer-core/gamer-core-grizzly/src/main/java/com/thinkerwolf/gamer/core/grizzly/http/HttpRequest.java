package com.thinkerwolf.gamer.core.grizzly.http;

import com.thinkerwolf.gamer.core.servlet.AbstractChRequest;
import com.thinkerwolf.gamer.core.servlet.Push;
import com.thinkerwolf.gamer.core.servlet.ServletConfig;
import com.thinkerwolf.gamer.remoting.Channel;
import com.thinkerwolf.gamer.remoting.Protocol;
import org.glassfish.grizzly.http.HttpRequestPacket;

import java.util.Map;

public class HttpRequest extends AbstractChRequest {

    private byte[] content;

    private HttpRequestPacket requestPacket;

    public HttpRequest(int requestId, String command, Channel ch, ServletConfig servletConfig) {
        super(requestId, command, ch, servletConfig);
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public Protocol getProtocol() {
        return Protocol.HTTP;
    }

    @Override
    public Push newPush() {
        return null;
    }

    public HttpRequestPacket getRequestPacket() {
        return requestPacket;
    }

    public static final class Builder {
        private int requestId;
        private String command;
        private Channel ch;
        private ServletConfig servletConfig;
        private Map<String, Object> attributes;
        private byte[] content;
        private HttpRequestPacket requestPacket;

        private Builder() {
        }

        public Builder setRequestPacket(HttpRequestPacket requestPacket) {
            this.requestPacket = requestPacket;
            return this;
        }

        public Builder setContent(byte[] content) {
            this.content = content;
            return this;
        }

        public Builder setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setRequestId(int requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder setCommand(String command) {
            this.command = command;
            return this;
        }

        public Builder setCh(Channel ch) {
            this.ch = ch;
            return this;
        }

        public Builder setServletConfig(ServletConfig servletConfig) {
            this.servletConfig = servletConfig;
            return this;
        }

        public HttpRequest build() {
            HttpRequest request = new HttpRequest(requestId, command, ch, servletConfig);
            request.content = content;
            request.requestPacket = requestPacket;
            if (attributes != null) {
                for (Map.Entry<String, Object> en : attributes.entrySet()) {
                    request.setAttribute(en.getKey(), en.getValue());
                }
            }
            return request;
        }
    }
}
