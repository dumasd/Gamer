package com.thinkerwolf.gamer.registry.etcd;

import com.thinkerwolf.gamer.common.URL;
import io.etcd.jetcd.ByteSequence;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class JetcdUtil {

    public static ByteSequence toByteSeq(String s) {
        return ByteSequence.from(s, UTF_8);
    }

    public static ByteSequence urlToByteSeq(URL url) {
        return toByteSeq(url.toString());
    }

    public static URL byteSeqToUrl(ByteSequence byteSequence) {
        try {
            return URL.parse(byteSequence.toString(UTF_8));
        } catch (Exception e) {
            return null;
        }
    }

    public static String byteSeqToString(ByteSequence byteSequence) {
        return byteSequence.toString(UTF_8);
    }

}
