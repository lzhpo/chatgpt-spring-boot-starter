package com.lzhpo.chatgpt.sse;

import com.luna.common.net.hander.AbstactEventSourceListener;
import com.luna.common.net.sse.Event;
import com.luna.common.net.sse.SseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author weidian
 * @description
 * @date 2023/4/28
 */
@Slf4j
public class CustomEventSourceListener extends AbstactEventSourceListener {
    public CustomEventSourceListener(FutureCallback<Event> eventCallBack, FutureCallback<SseResponse> resultCallback) {
        super(eventCallBack, resultCallback);
    }

}
