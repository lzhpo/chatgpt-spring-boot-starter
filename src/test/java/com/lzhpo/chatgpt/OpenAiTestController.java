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

import com.lzhpo.chatgpt.entity.chat.ChatCompletionRequest;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionResponse;
import com.lzhpo.chatgpt.sse.SseEventSourceListener;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author lzhpo
 */
@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class OpenAiTestController {

    private final OpenAiClient openAiClient;
    private final OpenAiKeyWrapper openAiKeyWrapper;

    @GetMapping("/page/chat")
    public ModelAndView chatView() {
        ModelAndView view = new ModelAndView("chat");
        view.addObject("apiKey", openAiKeyWrapper.wrap().next());
        return view;
    }

    @GetMapping("/page/chat/sse")
    public ModelAndView sseChatView() {
        return new ModelAndView("sse-stream-chat");
    }

    @GetMapping("/page/chat/websocket")
    public ModelAndView websocketChatView() {
        return new ModelAndView("websocket-stream-chat");
    }

    @ResponseBody
    @PostMapping("/chat")
    public ChatCompletionResponse chatCompletions(@RequestBody ChatCompletionRequest request) {
        return openAiClient.chatCompletions(request);
    }

    @ResponseBody
    @GetMapping("/chat/sse")
    public SseEmitter sseStreamChat(@RequestHeader Map<String, String> headers, @RequestParam String message) {
        SseEmitter sseEmitter = new SseEmitter();
        ChatCompletionRequest request = ChatCompletionRequest.create(message);
        openAiClient.streamChatCompletions(request, new SseEventSourceListener(sseEmitter));
        return sseEmitter;
    }
}
