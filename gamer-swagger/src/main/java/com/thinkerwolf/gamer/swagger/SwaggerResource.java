package com.thinkerwolf.gamer.swagger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

public class SwaggerResource implements Serializable, Comparable<SwaggerResource> {
    private String name;
    private String location;
    private String swaggerVersion;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("location")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @JsonProperty("swaggerVersion")
    public String getSwaggerVersion() {
        return swaggerVersion;
    }

    public void setSwaggerVersion(String swaggerVersion) {
        this.swaggerVersion = swaggerVersion;
    }

    @Override
    public int compareTo(SwaggerResource other) {
        return ComparisonChain.start()
                .compare(this.swaggerVersion, other.swaggerVersion)
                .compare(this.name, other.name)
                .result();
    }
}
