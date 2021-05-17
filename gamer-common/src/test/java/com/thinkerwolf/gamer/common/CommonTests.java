package com.thinkerwolf.gamer.common;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class CommonTests {
    @Test
    public void test1() {
        String str = "http://163.99.80.31:8080/com.thinkerwolf.rfddsaf/ccv";
        String res = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
        System.out.println(res);

        UUID uuid = UUID.nameUUIDFromBytes(str.getBytes(StandardCharsets.UTF_8));
        System.out.println(uuid.toString());

        uuid = UUID.nameUUIDFromBytes(str.getBytes(StandardCharsets.UTF_8));
        System.out.println(uuid.toString());
    }
}
