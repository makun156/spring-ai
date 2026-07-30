package com.business.springai.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("ai")
public class AiController {
    @Autowired
    ChatClient chatClient;

    @GetMapping("block")
    public String hi(@RequestParam(defaultValue = "你好") String question) {
        return chatClient.prompt().user(question).call().content();
    }

    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(defaultValue = "你好") String question) {
        return chatClient.prompt().user(question).advisors().stream().content();
    }

}
