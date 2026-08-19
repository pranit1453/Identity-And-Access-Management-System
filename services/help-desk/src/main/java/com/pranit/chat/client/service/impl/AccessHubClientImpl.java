package com.pranit.chat.client.service.impl;

import com.pranit.chat.client.service.AccessHubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AccessHubClientImpl implements AccessHubClient {

    private final WebClient webClient;

    @Override
    public boolean checkUserNameExists(final String username) {
        return Boolean.TRUE.equals(webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/profile/{username}/exists")
                        .build(username))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) return Mono.just(true);
                    if (response.statusCode().value() == 404) return Mono.just(false);
                    return response.createException().flatMap(Mono::error);
                })
                .block());
    }
}
