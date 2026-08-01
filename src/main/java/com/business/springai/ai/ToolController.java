package com.business.springai.ai;

import com.business.springai.tool.DateTimeUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("tool")
public class ToolController {
    @Autowired
    ChatClient chatClient;
    @Autowired
    DateTimeUtil dateTimeUtil;
    @GetMapping(value = "hiBlock")
    public String hiBlock(){
        return chatClient.prompt()
                .user("现在时间是？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                .tools(dateTimeUtil)
                .call()
                .content();
    }
    @GetMapping(value = "hiStream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> hiStream(){
        return chatClient.prompt()
                .user("帮我设置一个10分钟后的闹钟")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                .tools(dateTimeUtil)
                .stream()
                .content();
    }
}
