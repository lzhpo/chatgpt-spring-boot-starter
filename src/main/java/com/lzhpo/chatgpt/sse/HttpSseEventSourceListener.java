package com.lzhpo.chatgpt.sse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * @author luna
 * @description
 * @date 2023/4/28
 */
@Slf4j
public class HttpSseEventSourceListener<T,R> extends AbstractFutureCallback<T,R>  {

    private  SseEmitter sseEmitter;

    public HttpSseEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    @Override
    public void onEvent(R result) {
        try {
            sseEmitter.send(result);
        } catch (IOException e) {
            log.error("onEvent::result = {} ", result, e);
        }
    }

    @Override
    public void completed(T result) {
        sseEmitter.complete();
    }

    @Override
    public void failed(Exception ex) {
    }

    @Override
    public void cancelled() {
        super.cancelled();
    }

    public SseEmitter getSseEmitter() {
        return sseEmitter;
    }

    public void setSseEmitter(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }


}
