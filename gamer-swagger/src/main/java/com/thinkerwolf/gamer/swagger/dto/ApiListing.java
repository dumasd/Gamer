package com.thinkerwolf.gamer.swagger.dto;

import java.util.List;
import java.util.Set;

/**
 * 对应@Api注解
 *
 * @author wukai
 */
public class ApiListing {

    private String apiVersion;
    private String basePath;
    private boolean hidden;
    private Set<String> protocols;
    private List<ApiDescriptor> apis;
    private Set<String> tags;

    private String description = "apilisting";

    public ApiListing(String apiVersion,
                      String basePath,
                      boolean hidden,
                      Set<String> protocols,
                      List<ApiDescriptor> apis,
                      Set<String> tags) {
        this.apiVersion = apiVersion;
        this.basePath = basePath;
        this.hidden = hidden;
        this.protocols = protocols;
        this.apis = apis;
        this.tags = tags;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getBasePath() {
        return basePath;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Set<String> getProtocols() {
        return protocols;
    }

    public List<ApiDescriptor> getApis() {
        return apis;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }
}
