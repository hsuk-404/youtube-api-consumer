package com.hsuk.video.data.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:api.properties")
public class ApiConfigProperties {
    private final Environment env;

    @Autowired
    public ApiConfigProperties(Environment env) {
        this.env = env;
    }

    public String getKey() {
        return env.getProperty("api.key");
    }

    public String getPart() {
        return env.getProperty("api.part");
    }

    public String getChart() {
        return env.getProperty("api.chart");
    }

    public String getMaxResultPerRequest() {
        return env.getProperty("api.maxResultsPerRequest");
    }

    public String getMaxResultPerRegion() {
        return env.getProperty("api.maxResultsPerRegion");
    }

    public String getRegionCode() {
        return env.getProperty("api.regionCode");
    }

    public String getScheme() {
        return env.getProperty("api.scheme");
    }

    public String getHost() {
        return env.getProperty("api.host");
    }
}
