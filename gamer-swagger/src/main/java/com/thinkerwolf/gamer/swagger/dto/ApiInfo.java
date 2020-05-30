package com.thinkerwolf.gamer.swagger.dto;

/**
 * 项目信息
 *
 * @author wukai
 */
public class ApiInfo {
    private final String version;
    private final String title;
    private final String description;
    private final String termsOfServiceUrl;
    private final String license;
    private final String licenseUrl;
    private final Contact contact;


    public ApiInfo(
            String title,
            String description,
            String version,
            String termsOfServiceUrl,
            Contact contact,
            String license,
            String licenseUrl) {
        this.title = title;
        this.description = description;
        this.version = version;
        this.termsOfServiceUrl = termsOfServiceUrl;
        this.contact = contact;
        this.license = license;
        this.licenseUrl = licenseUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public Contact getContact() {
        return contact;
    }

    public String getLicense() {
        return license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public String getVersion() {
        return version;
    }

}
