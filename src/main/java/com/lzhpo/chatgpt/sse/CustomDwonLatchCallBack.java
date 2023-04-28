package com.lzhpo.chatgpt.sse;

import com.alibaba.fastjson2.JSON;
import com.luna.common.net.hander.AbstactEventFutureCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CountDownLatch;

/**
 * @author luna
 * @description
 * @date 2023/4/28
 */
@Slf4j
public class CustomDwonLatchCallBack<T,R> extends AbstractFutureCallback<T,R> implements Listener  {

    private final CountDownLatch countDownLatch;

    public CustomDwonLatchCallBack(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onEvent(R result) {
        log.info("onEvent::result = {}", JSON.toJSONString(result));
        countDownLatch.countDown();
    }

    @Override
    public void completed(T result) {
        super.completed(result);
    }

    @Override
    public void failed(Exception ex) {
        super.failed(ex);
    }

    @Override
    public void cancelled() {
        super.cancelled();
    }



}
