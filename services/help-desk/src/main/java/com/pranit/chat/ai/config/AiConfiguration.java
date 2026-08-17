package com.pranit.chat.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AiConfiguration {

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor memoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @Bean
    public List<Advisor> advisors(MessageChatMemoryAdvisor memoryAdvisor) {
        return List.of(
                memoryAdvisor,
                new SimpleLoggerAdvisor(),
                new SafeGuardAdvisor(List.of("env", "secret-key"))
        );
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, List<Advisor> advisors) {
        return builder
                .defaultSystem("Summarize the response within 1000 words.")
                .defaultAdvisors(advisors)
                .build();
    }
}
