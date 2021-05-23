package com.thinkerwolf.gamer.common.util;

import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CompressUtilTests {

    @Test
    public void test1() throws IOException {
        String s = "21344rcbmnfafef<gdsaf></fefasdf>";
        byte[] cc = CompressUtil.compress(s.getBytes(UTF_8), "gzip");
        byte[] de = CompressUtil.decompress(cc, "gzip");
        System.out.println(new String(de, UTF_8));
    }
}
