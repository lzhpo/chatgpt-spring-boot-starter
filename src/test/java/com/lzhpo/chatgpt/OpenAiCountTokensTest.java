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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.lang.Console;
import com.lzhpo.chatgpt.entity.CommonUsage;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionMessage;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionRequest;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionResponse;
import com.lzhpo.chatgpt.entity.completions.CompletionRequest;
import com.lzhpo.chatgpt.entity.completions.CompletionResponse;
import com.lzhpo.chatgpt.utils.JsonUtils;
import com.lzhpo.chatgpt.utils.TokenUtils;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author lzhpo
 */
@SpringBootTest
class OpenAiCountTokensTest {

    @Autowired
    private OpenAiClient openAiService;

    @MockBean
    private ServerEndpointExporter serverEndpointExporter;

    @Test
    void completionsTokens() {
        CompletionRequest request = new CompletionRequest();
        request.setModel("text-davinci-003");
        request.setPrompt("As an AI language model, I have generated this response by receiving your input.");
        request.setMaxTokens(7);
        request.setTemperature(0);

        Long tokens = TokenUtils.tokens(request.getModel(), request.getPrompt());
        Console.log("Count tokens for request: {}", tokens);

        CompletionResponse response = openAiService.completions(request);
        assertNotNull(response);

        CommonUsage usage = response.getUsage();
        Long promptTokens = usage.getPromptTokens();
        Console.log("Prompt tokens: {}", promptTokens);
        Console.log("Completion tokens: {}", usage.getCompletionTokens());
        Console.log("Total tokens: {}", usage.getTotalTokens());

        assertEquals(promptTokens, tokens);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    void chatCompletionsTokens() {
        List<ChatCompletionMessage> messages = new ArrayList<>();

        ChatCompletionMessage message1 = new ChatCompletionMessage();
        message1.setRole("user");
        message1.setContent("What's your name?");
        messages.add(message1);

        ChatCompletionMessage message2 = new ChatCompletionMessage();
        message2.setRole("user");
        message2.setContent("How old are you?");
        messages.add(message2);

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(messages);

        Long reqTokens = TokenUtils.tokens(request.getModel(), request.getMessages());
        Console.log("Count input tokens: {}", reqTokens);

        ChatCompletionResponse response = openAiService.chatCompletions(request);
        assertNotNull(response);

        CommonUsage usage = response.getUsage();
        assertNotNull(usage);
        Long promptTokens = usage.getPromptTokens();
        Console.log("Prompt tokens: {}", promptTokens);
        Console.log("Completion tokens: {}", usage.getCompletionTokens());
        Console.log("Total tokens: {}", usage.getTotalTokens());

        Console.log(JsonUtils.toJsonPrettyString(response));
        assertEquals(promptTokens, reqTokens);
    }
}
