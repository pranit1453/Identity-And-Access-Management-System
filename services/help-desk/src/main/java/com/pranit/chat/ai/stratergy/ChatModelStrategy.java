package com.pranit.chat.ai.stratergy;

import com.pranit.chat.dto.QueryResponse;
import com.pranit.chat.entities.constant.Provider;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChatModelStrategy {

    QueryResponse getResponse(String query, UUID conversationId);

    Flux<QueryResponse> getStreamResponse(String query, UUID conversionId);

    Provider getProviderName();

}
