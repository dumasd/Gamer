package com.thinkerwolf.gamer.remoting;

import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;

import java.io.IOException;

public interface Codec {

    Object decode(Channel channel, ChannelBuffer buf) throws IOException;

    void encode(Channel channel, ChannelBuffer buf, Object message) throws IOException;

    enum DecodeResult {
        NEED_MORE_INPUT
    }

}
