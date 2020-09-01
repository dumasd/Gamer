package com.thinkerwolf.gamer.common;

import com.thinkerwolf.gamer.common.util.ResourceUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ResourceTests {

    public void testUrl() {

    }

    @Test
    public void testResourceUtils() throws IOException {
        ResourceUtils.findClasspathFilePaths("org/junit", "class");
        InputStream is = ResourceUtils.findInputStream("org/junit", "Test.class");
    }

    @Test
    public void testHttpResource() throws IOException {
        //https://b.ivsky.com/g2dj/yabs/5n/yabs.js?ej1rdo=o47g2dj_owdh7eoe647_pah
        InputStream is = ResourceUtils.findInputStream("https://b.ivsky.com", "g2dj/yabs/5n/yabs.js?ej1rdo=o47g2dj_owdh7eoe647_pah");
        System.out.println(Arrays.toString(IOUtils.readFully(is, is.available())));
    }

}
