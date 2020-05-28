package com.thinkerwolf.gamer.swagger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DocumentCache {

    private Map<String, Document> cache = new ConcurrentHashMap<>();


    public void addCache(Document document) {
        cache.put(document.getGroupName(), document);
    }

    public Document getCache(String groupName) {
        return cache.get(groupName);
    }

}
