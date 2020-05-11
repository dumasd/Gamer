package com.thinkerwolf.gamer.common.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public final class CharsetUtil {

    public static CharsetEncoder getEncoder(Charset charset) {
        return charset.newEncoder();
    }

    public static CharsetDecoder getDecoder(Charset charset) {
        return charset.newDecoder();
    }

}
