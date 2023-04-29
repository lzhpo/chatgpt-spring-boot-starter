package com.lzhpo.chatgpt.sse;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.CountDownLatch;

/**
 * @author luna
 * @description
 * @date 2023/4/23
 */
@Slf4j
public class CustomDownLatchEventFutureCallback<T, R> extends AbstractFutureCallback<T, R> implements Listener {

    private final CountDownLatch countDownLatch;

    public CustomDownLatchEventFutureCallback(CountDownLatch countDownLatch) {
        Assert.notNull(countDownLatch, "countDownLatch cannot null.");
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void completed(T result) {
        log.info("completed::result = {}", JSON.toJSONString(result));
        countDownLatch.countDown();
    }

    @Override
    public void failed(Exception ex) {
        super.failed(ex);
        cancelled();
        countDownLatch.countDown();
    }

    @Override
    public void cancelled() {
        super.cancelled();
        countDownLatch.countDown();
    }
}
