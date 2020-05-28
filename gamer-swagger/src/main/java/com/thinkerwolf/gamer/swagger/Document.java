package com.thinkerwolf.gamer.swagger;

import com.google.common.collect.Sets;
import com.thinkerwolf.gamer.swagger.dto.ApiListing;
import com.thinkerwolf.gamer.swagger.dto.Tag;

import java.util.Map;
import java.util.Set;

/**
 * 文档
 *
 * @author wukai
 */
public class Document {

    private String groupName;

    private Map<String, ApiListing> apiListingMap;

    private Set<Tag> tags;

    public Document(String groupName, Map<String, ApiListing> apiListingMap) {
        this.groupName = groupName;
        this.apiListingMap = apiListingMap;
        this.tags = Sets.newHashSet();
        for (ApiListing listing : apiListingMap.values()) {
            for (String name : listing.getTags()) {
                tags.add(new Tag(name, listing.getDescription()));
            }
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public Map<String, ApiListing> getApiListingMap() {
        return apiListingMap;
    }

    public Set<Tag> getTags() {
        return tags;
    }
}
