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

import com.lzhpo.chatgpt.exception.OpenAiException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract listener for <a href="https://www.w3.org/TR/eventsource/">Server-Sent Events</a>.
 *
 * @author lzhpo
 */
@Slf4j
public class AbstractEventSourceListener extends EventSourceListener {

    @Override
    public void onClosed(@NotNull EventSource eventSource) {
        log.debug("Execute onClosed method.");
    }

    @Override
    public void onEvent(
            @NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
        log.debug("Execute onEvent method.");
        log.debug("id: {}, type: {}, data: {}", id, type, data);
    }

    @Override
    public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable e, @Nullable Response response) {
        String errorMsg = Optional.ofNullable(e)
                .map(Throwable::getMessage)
                .orElse(Optional.ofNullable(response)
                        .map(Response::body)
                        .map(ResponseBody::source)
                        .map(bufferedSource -> {
                            try {
                                bufferedSource.request(Long.MAX_VALUE);
                            } catch (Exception ex) {
                                throw new OpenAiException(ex);
                            }
                            return bufferedSource;
                        })
                        .map(BufferedSource::getBuffer)
                        .map(buffer -> buffer.clone().readString(StandardCharsets.UTF_8))
                        .orElse("Unexpected exception"));
        throw new OpenAiException(errorMsg);
    }

    @Override
    public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
        log.debug("Execute onOpen method, response: {}", response);
    }
}
