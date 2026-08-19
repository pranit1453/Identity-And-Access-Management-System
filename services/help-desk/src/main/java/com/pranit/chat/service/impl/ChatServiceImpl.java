package com.pranit.chat.service.impl;

import com.pranit.chat.ai.factory.ChatModelProviderFactory;
import com.pranit.chat.dto.QueryResponse;
import com.pranit.chat.entities.constant.Provider;
import com.pranit.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public final class ChatServiceImpl implements ChatService {

    private final ChatModelProviderFactory factory;

    @Override
    public QueryResponse getResponseFromAssistant(final Provider provider, final String query, final UUID conversationId) {
        return factory.getStrategy(provider).getResponse(query, conversationId);
    }

    @Override
    public Flux<QueryResponse> getStreamResponseFromAssistant(final Provider provider, final String query, final UUID conversationId) {
        return factory.getStrategy(provider).getStreamResponse(query, conversationId);
    }
}
