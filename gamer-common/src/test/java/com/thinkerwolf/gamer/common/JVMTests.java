package com.thinkerwolf.gamer.common;

import org.junit.Test;

public class JVMTests {

    @Test
    public void testString() {
//        String s11 = new StringBuilder().append("我是一个").toString();
        String s12 = "我是一个";

        String s13 = "我是一个";
        System.out.println("s11==s13 ? " + (s13 == s12));


        String s21 = new StringBuilder("ja").append("va").toString();
        String s22  = "java";
        System.out.println("s21==s22 ? " + (s21 == s22));

    }

}
