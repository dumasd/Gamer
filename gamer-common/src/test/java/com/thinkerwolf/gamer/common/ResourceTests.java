package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.ResourceUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ResourceTests {

    public void testUrl() {

    }

    @Test
    public void testResourceUtils() throws IOException {
        ResourceUtils.findClasspathFilePaths("org/junit", "class");
        InputStream is = ResourceUtils.findInputStream("org/junit", "Test.class");
    }

    @Test
    public void testFile() {
        File file = new File("./target/gamer-common-1.0.0.jar");
        System.out.println(file.exists());
    }


    public static void main(String[] args) {
        new ResourceTests().testFile();
    }
}
