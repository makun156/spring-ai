package com.business.springai.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("structOutput")
public class StructOutputController {
    @Autowired
    ChatClient chatClient;

    @GetMapping(value = "block")
    public String block(@RequestParam(defaultValue = "你好") String question) {
        Story story=chatClient.prompt()
                .user(question)
                .call()
                .entity(Story.class);
        return story.toString();
    }
    public record Story(String name, List<String> stories){}
}
