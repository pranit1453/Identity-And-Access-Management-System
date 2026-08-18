package com.pranit.chat.service;

import com.pranit.chat.dto.QueryResponse;
import com.pranit.chat.entities.constant.Provider;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatService {

    QueryResponse getResponseFromAssistant(Provider provider, String query, UUID conversationId);

    Flux<QueryResponse> getStreamResponseFromAssistant(Provider provider, String query, UUID conversationId);

}
