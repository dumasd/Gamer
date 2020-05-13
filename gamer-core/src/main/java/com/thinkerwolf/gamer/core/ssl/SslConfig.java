package com.thinkerwolf.gamer.core.ssl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SslConfig {

    private boolean enabled;

    private String keyStore;

    private String keyStorePassword;

    private String trustStore;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SslConfig sslConfig = (SslConfig) o;

        return new EqualsBuilder()
                .append(keyStore, sslConfig.keyStore)
                .append(keyStorePassword, sslConfig.keyStorePassword)
                .append(trustStore, sslConfig.trustStore)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(keyStore)
                .append(keyStorePassword)
                .append(trustStore)
                .toHashCode();
    }

}
