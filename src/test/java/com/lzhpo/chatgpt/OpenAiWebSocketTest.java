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

package com.lzhpo.chatgpt;

import cn.hutool.extra.spring.SpringUtil;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionRequest;
import com.lzhpo.chatgpt.sse.WebSocketEventSourceListener;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author lzhpo
 */
@Slf4j
@Component
@ServerEndpoint("/chat/websocket")
public class OpenAiWebSocketTest {

    @OnOpen
    public void onOpen(Session session) {
        log.info("sessionId={} joined.", session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("Received sessionId={} message={}", session.getId(), message);
        ChatCompletionRequest request = ChatCompletionRequest.create(message);
        WebSocketEventSourceListener listener = new WebSocketEventSourceListener(session);
        SpringUtil.getBean(OpenAiClient.class).streamChatCompletions(request, listener);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Closed sessionId={} connection.", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("sessionId={} error: {}", session.getId(), e.getMessage(), e);
    }
}
