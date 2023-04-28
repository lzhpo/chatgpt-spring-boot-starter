package com.lzhpo.chatgpt.sse;

import com.luna.common.net.hander.AbstactEventFutureCallback;
import com.luna.common.net.sse.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author luna
 * @description
 * @date 2023/4/28
 */
@Slf4j
public class CustomSseFutureCallback<T,R> extends AbstactEventFutureCallback<T,R> implements Listener {
    private final SseEmitter sseEmitter;

    public CustomSseFutureCallback(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void completed(T result) {
        try {
            System.out.println(result);
            sseEmitter.send(result2SseEmitter((Event) result));
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
