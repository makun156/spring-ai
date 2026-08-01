package com.business.springai.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

/**
 * Redis-backed implementation of {@link ChatMemoryRepository}.
 * <p>
 * Stores chat messages per conversation as JSON strings in a Redis List.
 * Uses manual serialization instead of relying on Jackson's polymorphic
 * deserialization, because Spring AI's Message classes (UserMessage,
 * AssistantMessage, etc.) are immutable and lack Jackson-compatible constructors.
 * Each conversation key has a 7-day TTL.
 */
@Component
public class RedisChatMemoryRepository implements ChatMemoryRepository {

    private static final String KEY_PREFIX = "chat:memory:";
    private static final Duration TTL = Duration.ofDays(7);

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisChatMemoryRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<String> findConversationIds() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return keys.stream()
                .map(key -> key.substring(KEY_PREFIX.length()))
                .toList();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        String key = KEY_PREFIX + conversationId;
        List<String> jsons = redisTemplate.opsForList().range(key, 0, -1);
        if (jsons == null || jsons.isEmpty()) {
            return Collections.emptyList();
        }
        return jsons.stream()
                .map(this::deserialize)
                .toList();
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages) {
        String key = KEY_PREFIX + conversationId;
        redisTemplate.delete(key);
        if (messages != null && !messages.isEmpty()) {
            List<String> jsons = messages.stream()
                    .map(this::serialize)
                    .toList();
            redisTemplate.opsForList().rightPushAll(key, jsons);
            redisTemplate.expire(key, TTL);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId) {
        redisTemplate.delete(KEY_PREFIX + conversationId);
    }

    // ── Serialization ────────────────────────────────────────────────

    private String serialize(Message message) {
        try {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("messageType", message.getMessageType().name());
            dto.put("text", message.getText());
            dto.put("metadata", message.getMetadata());

            if (message instanceof AssistantMessage assistant && assistant.hasToolCalls()) {
                dto.put("toolCalls", assistant.getToolCalls().stream()
                        .map(tc -> Map.of(
                                "id", tc.id(),
                                "type", tc.type(),
                                "name", tc.name(),
                                "arguments", tc.arguments()))
                        .toList());
            }

            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize message", e);
        }
    }

    // ── Deserialization ──────────────────────────────────────────────

    private Message deserialize(String json) {
        try {
            Map<String, Object> dto = objectMapper.readValue(json, new TypeReference<>() {});
            String type = (String) dto.get("messageType");
            String text = (String) dto.getOrDefault("text", "");
            @SuppressWarnings("unchecked")
            Map<String, Object> metadata = (Map<String, Object>) dto.getOrDefault("metadata", Map.of());

            return switch (type) {
                case "USER" -> UserMessage.builder()
                        .text(text)
                        .metadata(metadata)
                        .build();

                case "ASSISTANT" -> {
                    AssistantMessage.Builder<?> builder = AssistantMessage.builder()
                            .content(text)
                            .properties(metadata);
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> rawToolCalls =
                            (List<Map<String, Object>>) dto.get("toolCalls");
                    if (rawToolCalls != null && !rawToolCalls.isEmpty()) {
                        builder.toolCalls(rawToolCalls.stream()
                                .map(tc -> new AssistantMessage.ToolCall(
                                        (String) tc.get("id"),
                                        (String) tc.get("type"),
                                        (String) tc.get("name"),
                                        (String) tc.get("arguments")))
                                .toList());
                    }
                    yield builder.build();
                }

                case "SYSTEM" -> SystemMessage.builder()
                        .text(text)
                        .metadata(metadata)
                        .build();

                default -> throw new RuntimeException("Unknown message type: " + type);
            };
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}
