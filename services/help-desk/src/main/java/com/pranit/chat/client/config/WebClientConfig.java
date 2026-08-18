package com.pranit.chat.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(
            WebClient.Builder builder,
            @Value("${access-hub.base-url}") String baseUrl,
            @Value("${access-hub.api-version}") String apiVersion) {
        return builder
                .baseUrl(baseUrl)
                .defaultHeader("X-API-VERSION", apiVersion)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}