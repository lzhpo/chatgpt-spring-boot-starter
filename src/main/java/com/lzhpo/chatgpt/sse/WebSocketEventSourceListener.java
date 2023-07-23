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

import javax.websocket.Session;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import org.springframework.util.Assert;

/**
 * WebSocket with <a href="https://www.w3.org/TR/eventsource/">Server-Sent Events</a>.
 *
 * @author lzhpo
 */
@Slf4j
public class WebSocketEventSourceListener extends AbstractEventSourceListener {

    private final Session session;

    public WebSocketEventSourceListener(Session session) {
        Assert.notNull(session, "WebSocket session cannot null.");
        this.session = session;
    }

    @Override
    @SneakyThrows
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        super.onEvent(eventSource, id, type, data);
        session.getBasicRemote().sendText(data);
    }
}
