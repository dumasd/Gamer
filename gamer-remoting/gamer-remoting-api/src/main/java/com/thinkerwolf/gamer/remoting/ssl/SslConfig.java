package com.thinkerwolf.gamer.remoting.ssl;

import java.io.Serializable;
import java.util.Objects;

/**
 * Ssl 配置
 *
 * @author wukai
 */
public class SslConfig implements Serializable {
    /**
     * 是否启用SSL
     */
    private boolean enabled;
    /**
     * 密钥文件
     */
    private String keystoreFile;
    /**
     * 密钥文件密码
     */
    private String keystorePass;
    /**
     * 信任证书文件
     */
    private String truststoreFile;
    /**
     * 信任证书密码
     */
    private String truststorePass;

    public static Builder builder() {
        return new Builder();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getKeystoreFile() {
        return keystoreFile;
    }

    public void setKeystoreFile(String keystoreFile) {
        this.keystoreFile = keystoreFile;
    }

    public String getKeystorePass() {
        return keystorePass;
    }

    public void setKeystorePass(String keystorePass) {
        this.keystorePass = keystorePass;
    }

    public String getTruststoreFile() {
        return truststoreFile;
    }

    public void setTruststoreFile(String truststoreFile) {
        this.truststoreFile = truststoreFile;
    }

    public String getTruststorePass() {
        return truststorePass;
    }

    public void setTruststorePass(String truststorePass) {
        this.truststorePass = truststorePass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SslConfig sslConfig = (SslConfig) o;
        return enabled == sslConfig.enabled &&
                Objects.equals(keystoreFile, sslConfig.keystoreFile) &&
                Objects.equals(keystorePass, sslConfig.keystorePass) &&
                Objects.equals(truststoreFile, sslConfig.truststoreFile) &&
                Objects.equals(truststorePass, sslConfig.truststorePass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, keystoreFile, keystorePass, truststoreFile, truststorePass);
    }

    public static class Builder {
        private boolean enabled;
        private String keystoreFile;
        private String keystorePass;
        private String truststoreFile;
        private String truststorePass;

        private Builder() {
        }


        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setKeystoreFile(String keystoreFile) {
            this.keystoreFile = keystoreFile;
            return this;
        }

        public Builder setKeystorePass(String keystorePass) {
            this.keystorePass = keystorePass;
            return this;
        }

        public Builder setTruststoreFile(String truststoreFile) {
            this.truststoreFile = truststoreFile;
            return this;
        }

        public Builder setTruststorePass(String truststorePass) {
            this.truststorePass = truststorePass;
            return this;
        }

        public SslConfig build() {
            SslConfig sslConfig = new SslConfig();
            sslConfig.setEnabled(enabled);
            sslConfig.setKeystoreFile(keystoreFile);
            sslConfig.setKeystorePass(keystorePass);
            sslConfig.setTruststoreFile(truststoreFile);
            sslConfig.setTruststorePass(truststorePass);
            return sslConfig;
        }
    }

}
