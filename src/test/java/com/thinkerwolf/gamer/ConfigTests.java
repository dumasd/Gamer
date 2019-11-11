package com.thinkerwolf.gamer;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ConfigTests {

    @Test
    public void snakeYaml() {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(getClass().getClassLoader().getResourceAsStream("conf.yaml"));
        System.out.println(obj.get("servlet"));
        System.out.println(obj.get("netty"));
    }

}
