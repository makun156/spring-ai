package com.business.springai.ai;

import jakarta.websocket.server.PathParam;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("memory")
public class MemoryController {
    @Autowired
    ChatClient chatClient;
    @GetMapping(value = "hi",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> hi(@RequestParam(defaultValue = "1") String userId, String question){
        return chatClient.prompt().advisors(advisorSpec -> advisorSpec.params(Map.of(ChatMemory.CONVERSATION_ID,userId))).user(question).stream().content();
    }
}
