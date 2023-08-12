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

package com.lzhpo.chatgpt.entity.chat;

import cn.hutool.core.collection.ListUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lzhpo.chatgpt.entity.CommonConfig;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author lzhpo
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest extends CommonConfig {

    /**
     * ID of the model to use.
     *
     * <p>You can use the <a href="https://platform.openai.com/docs/api-reference/models/list">List models</a> API to see all of your available models,
     * or see our <a href="https://platform.openai.com/docs/models/overview">Model overview</a> for descriptions of them.
     * <a href="https://platform.openai.com/docs/models/model-endpoint-compatibility">Model endpoint compatibility</a>
     */
    @NotBlank
    @Builder.Default
    private String model = "gpt-3.5-turbo";

    /**
     * The messages to generate chat completions for, in the <a href="https://platform.openai.com/docs/guides/chat/introduction">chat format</a>.
     */
    @Valid
    @NotEmpty
    private List<ChatCompletionMessage> messages;

    /**
     * A list of functions the model may generate JSON inputs for.
     * <a href="https://openai.com/blog/function-calling-and-other-api-updates">function-calling-and-other-api-updates</a>
     */
    private List<ChatCompletionFunction> functions;

    /**
     * Controls how the model responds to function calls. "none" means the model does not call a function,
     * <p>and responds to the end-user. "auto" means the model can pick between an end-user or calling a function.
     * <p>Specifying a particular function via {"name":\ "my_function"} forces the model to call that function.
     * <ul>
     *     <li> "none" is the default when no functions are present. </li>
     *     <li> "auto" is the default if functions are present. </li>
     * </ul>
     */
    @JsonProperty("function_call")
    private Object functionCall;

    /**
     * If set, partial message deltas will be sent, like in ChatGPT.
     * Tokens will be sent as data-only <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format">server-sent events</a> as they become available, with the stream terminated by a {@code data: [DONE]} message.
     *
     * <p><a href="https://github.com/openai/openai-cookbook/blob/main/examples/How_to_stream_completions.ipynb">See the OpenAI Cookbook for example code.</a>
     */
    private Boolean stream;

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     */
    private String stop;

    /**
     * The maximum number of <a href="https://platform.openai.com/tokenizer">tokens</a> to generate in the chat completion. Defaults to inf.
     *
     * <p>The total length of input tokens and generated tokens is limited by the model's context length.
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they appear in the text so far,
     * increasing the model's likelihood to talk about new topics.
     *
     * <p><a href="https://platform.openai.com/docs/api-reference/parameter-details">See more information about frequency and presence penalties.</a>
     */
    @Min(-2)
    @Max(2)
    @JsonProperty("presence_penalty")
    private Number presencePenalty;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing frequency in the text so far,
     * decreasing the model's likelihood to repeat the same line verbatim.
     *
     * <p><a href="https://platform.openai.com/docs/api-reference/parameter-details">See more information about frequency and presence penalties.</a>
     */
    @Min(-2)
    @Max(2)
    @JsonProperty("frequency_penalty")
    private Number frequencyPenalty;

    /**
     * Modify the likelihood of specified tokens appearing in the completion.
     *
     * <p>Accepts a json object that maps tokens (specified by their token ID in the tokenizer) to an associated bias value from -100 to 100.
     * Mathematically, the bias is added to the logits generated by the model prior to sampling.
     * The exact effect will vary per model, but values between -1 and 1 should decrease or increase likelihood of selection; values like -100 or 100 should result in a ban or exclusive selection of the relevant token.
     */
    @JsonProperty("logit_bias")
    private Map<Object, Object> logitBias;

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     *
     * <p><a href="https://platform.openai.com/docs/guides/safety-best-practices/end-user-ids">Learn more.</a>
     */
    private String user;

    /**
     * User {@code content} to create one chat request.
     *
     * @param content content
     */
    public static ChatCompletionRequest create(String content) {
        ChatCompletionMessage message = new ChatCompletionMessage();
        message.setContent(content);
        return ChatCompletionRequest.builder().messages(ListUtil.of(message)).build();
    }
}
