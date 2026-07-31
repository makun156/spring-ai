package com.business.springai.config;

import com.business.springai.advisor.CustomAdvisor1;
import com.business.springai.advisor.CustomAdvisor2;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel,
                                 CustomAdvisor1 customAdvisor1,
                                 CustomAdvisor2 customAdvisor2) {
        SimpleLoggerAdvisor logAdvisor = SimpleLoggerAdvisor.builder().build();

        return ChatClient.builder(chatModel)
                .defaultAdvisors(logAdvisor, customAdvisor1, customAdvisor2)
                .build();
    }
}
