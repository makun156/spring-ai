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
public class CustomAdvisor1 implements StreamAdvisor {
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        Map<String, @Nullable Object> context = chatClientRequest.context();
        Object customValue = context.get("custom1");
        if (Objects.isNull(customValue)) {
            log.error("自定义advisor1为空");
            return streamAdvisorChain.nextStream(chatClientRequest);
        }
        log.info("执行业务1逻辑");
        // 3. 调用下游并处理响应
        return streamAdvisorChain.nextStream(chatClientRequest)
                // 调用后处理 - 每个响应片段
                .doOnNext(response -> {
                    if (response.chatResponse() != null && response.chatResponse().getResult() != null) {
                        String content = response.chatResponse().getResult().getOutput().getText();
                        log.debug("业务1收到响应片段: {}", content);
                    }
                })
                // 调用后处理 - 流完成
                .doOnComplete(() -> {
                    log.info("业务1处理完成，参数: {}", customValue);
                })
                // 调用后处理 - 错误
                .doOnError(error -> {
                    log.error("业务1处理出错", error);
                });
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
