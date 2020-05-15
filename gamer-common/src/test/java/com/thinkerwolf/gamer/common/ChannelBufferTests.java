package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.buffer.ChannelBuffer;
import com.thinkerwolf.gamer.common.buffer.ChannelBuffers;
import org.junit.Test;

public class ChannelBufferTests {

    @Test
    public void testBasic() {
        try {
//            ChannelBuffer buffer = ChannelBuffers.buffer(2);
            ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
            buffer.writeByte(1);
            buffer.writeBytes(new byte[]{2, 8, 9, 10});


        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}
