package com.business.springai.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class CustomAdvisor2 implements StreamAdvisor {
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        Map<String, @Nullable Object> context = chatClientRequest.context();
        Object customValue = context.get("custom2");
        if (Objects.isNull(customValue)) {
            log.error("自定义advisor2为空");
            return streamAdvisorChain.nextStream(chatClientRequest);
        }
        log.info("执行业务2逻辑");
        return streamAdvisorChain.nextStream(chatClientRequest);
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
