package com.business.springai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        SimpleLoggerAdvisor logAdvisor = SimpleLoggerAdvisor.builder().build();
        MessageWindowChatMemory buildChatMemory = MessageWindowChatMemory.builder().build();
        MessageChatMemoryAdvisor buildChatMemoryAdvisor = MessageChatMemoryAdvisor.builder(buildChatMemory).build();
        return ChatClient.builder(chatModel)
                .defaultAdvisors(logAdvisor,buildChatMemoryAdvisor)
                .build();
    }
}
