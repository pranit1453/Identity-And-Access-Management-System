package com.pranit.chat.ai.provider;

import com.pranit.chat.agent.tool.SystemTool;
import com.pranit.chat.agent.tool.TicketTool;
import com.pranit.chat.ai.stratergy.ChatModelStrategy;
import com.pranit.chat.dto.QueryResponse;
import com.pranit.chat.entities.constant.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiService implements ChatModelStrategy {

    private final ChatClient chatClient;
    private final SystemTool systemTool;
    private final TicketTool ticketTool;
    @Value("classpath:prompts/helpdesk-system.st")
    private Resource systemPrompt;

    @Override
    public QueryResponse getResponse(String query, UUID conversationId) {
        final ChatResponse response = this.chatClient.prompt()
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .tools(systemTool, ticketTool)
                .system(systemPrompt)
                .user(query)
                .call()
                .chatResponse();
        final String message = Optional.ofNullable(response)
                .map(ChatResponse::getResult)
                .map(Generation::getOutput)
                .map(AbstractMessage::getText)
                .orElse("Sorry, I could not generate a response.");
        return QueryResponse.builder()
                .message(message)
                .build();
    }

    @Override
    public Flux<QueryResponse> getStreamResponse(String query, UUID conversionId) {
        return null;
    }

    @Override
    public Provider getProviderName() {
        return Provider.NVIDIA;
    }
}
