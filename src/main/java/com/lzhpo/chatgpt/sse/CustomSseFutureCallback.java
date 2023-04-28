package com.lzhpo.chatgpt.sse;

import com.luna.common.net.async.CustomAbstacktFutureCallback;
import com.luna.common.net.sse.Event;
import com.luna.common.net.sse.SseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author weidian
 * @description
 * @date 2023/4/28
 */
@Slf4j
public class CustomSseFutureCallback extends CustomAbstacktFutureCallback<Event> implements Listener {
    private final SseEmitter sseEmitter;

    public CustomSseFutureCallback(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void completed(Event result) {
        try {
            sseEmitter.send(result2SseEmitter(result));
        } catch (IOException e) {
            log.error("completed::result = {} ", result, e);
        }
    }

    @Override
    public void failed(Exception ex) {
        log.error("failed::ex", ex);
    }

    @Override
    public void cancelled() {
        sseEmitter.complete();
    }

    public static SseEmitter.SseEventBuilder result2SseEmitter(Event result) {
        SseEmitter.SseEventBuilder event = SseEmitter.event();
        event.id(result.getId());
        event.name(result.getEvent());
        event.data(result.getData());
        event.reconnectTime(result.getRetry());
        return event;
    }
}
