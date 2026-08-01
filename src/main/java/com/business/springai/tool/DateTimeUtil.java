package com.business.springai.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Slf4j
@Component
public class DateTimeUtil {
    @Tool(name = "getCurrentTime", description = "获取当前时间")
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
    @Tool(name = "setAlarm", description = "设置闹钟，调用此工具可在指定时间触发提醒")
    public String setAlarm(@ToolParam(description = "闹钟的触发时间，格式年:月:日 时:分:秒") String time) {
        log.info("setAlarm:{}",time);
        return "好的，已设置闹钟";
    }
}
