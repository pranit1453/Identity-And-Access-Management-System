package com.pranit.accesshub.security.config;

import com.pranit.accesshub.security.service.impl.VersionParser;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class VersionConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .useRequestHeader("X-API-Version")
                .setVersionParser(new VersionParser())
                .addSupportedVersions("v1")
                .setDefaultVersion("v1")
                .detectSupportedVersions(false);
    }
}
