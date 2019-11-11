package com.thinkerwolf.gamer;

import com.thinkerwolf.gamer.core.servlet.Protocol;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

public class ConfigTests {

    @Test
    public void snakeYaml() {
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(getClass().getClassLoader().getResourceAsStream("conf.yaml"));
        System.out.println(obj.get("servlet"));


        List<Map<String, Object>> list = (List<Map<String, Object>>) obj.get("netty");
        Map<String, Object> map = list.get(0);
        System.out.println(map.get("bossThreads").getClass());

        System.out.println(Protocol.valueOf("YYY"));

    }

}
