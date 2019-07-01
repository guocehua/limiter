package com.ibs.limiter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties(prefix = "limiter")
public class LimiterConfig {
    @NotNull
    private Double permitsPerSecond;
    @NotBlank
    private String key;

    private Double maxBurstSeconds;

    public Double getPermitsPerSecond() {
        return permitsPerSecond;
    }

    public void setPermitsPerSecond(Double permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getMaxBurstSeconds() {
        return maxBurstSeconds;
    }

    public void setMaxBurstSeconds(Double maxBurstSeconds) {
        this.maxBurstSeconds = maxBurstSeconds;
    }
}
