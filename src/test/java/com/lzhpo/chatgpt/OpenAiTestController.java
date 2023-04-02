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

import cn.hutool.core.lang.WeightRandom;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionRequest;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionResponse;
import com.lzhpo.chatgpt.sse.SseEventSourceListener;
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
    private final WeightRandom<String> apiKeyWeightRandom;

    @GetMapping("/page/chat")
    public ModelAndView chatView() {
        ModelAndView view = new ModelAndView("chat");
        view.addObject("apiKey", apiKeyWeightRandom.next());
        return view;
    }

    @GetMapping("/page/chat/stream")
    public ModelAndView streamChatView() {
        return new ModelAndView("stream-chat");
    }

    @ResponseBody
    @PostMapping("/chat")
    public ChatCompletionResponse chatCompletions(@RequestBody ChatCompletionRequest request) {
        return openAiClient.chatCompletions(request);
    }

    @ResponseBody
    @GetMapping("/chat/stream")
    public SseEmitter streamChatCompletions(@RequestParam String content) {
        SseEmitter sseEmitter = new SseEmitter();
        ChatCompletionRequest request = new ChatCompletionRequest(content);
        openAiClient.streamChatCompletions(request, new SseEventSourceListener(sseEmitter));
        return sseEmitter;
    }
}