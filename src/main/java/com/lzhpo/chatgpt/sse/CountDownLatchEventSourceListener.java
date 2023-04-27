/*
 * Copyright 2023 lzhpo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzhpo.chatgpt.sse;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

/**
 * {@link CountDownLatch} with <a href="https://www.w3.org/TR/eventsource/">Server-Sent Events</a>.
 *
 * @author lzhpo
 */
@Slf4j
public class CountDownLatchEventSourceListener extends AbstractEventSourceListener {

    private final CountDownLatch countDownLatch;

    public CountDownLatchEventSourceListener(CountDownLatch countDownLatch) {
        Assert.notNull(countDownLatch, "countDownLatch cannot null.");
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        super.onClosed(eventSource);
        eventSource.cancel();
        countDownLatch.countDown();
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable e, @Nullable Response response) {
        eventSource.cancel();
        countDownLatch.countDown();
        super.onFailure(eventSource, e, response);
    }
}
