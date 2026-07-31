package com.business.springai.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("prompt")
public class PromptController {
    @Autowired
    ChatClient chatClient;

    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(defaultValue = "你好") String question) {
        return chatClient.prompt()
                .system("你是一个全能的法律小助手,只能回答关于法律的知识，其余问题一律拒绝回答")
                .user(question)
                .advisors(
                        advisorSpec -> advisorSpec.params(Map.of("custom1", "custom1"))
                )
                .stream()
                .content();
    }
}
