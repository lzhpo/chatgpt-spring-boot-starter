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

package com.lzhpo.chatgpt.utils;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionMessage;
import com.lzhpo.chatgpt.exception.OpenAiException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * <a href="https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb">How_to_count_tokens_with_tiktoken</a>
 * <pre>
 * {@code
 *   def num_tokens_from_messages(messages, model="gpt-3.5-turbo-0301"):
 *     """Returns the number of tokens used by a list of messages."""
 *     try:
 *         encoding = tiktoken.encoding_for_model(model)
 *     except KeyError:
 *         print("Warning: model not found. Using cl100k_base encoding.")
 *         encoding = tiktoken.get_encoding("cl100k_base")
 *     if model == "gpt-3.5-turbo":
 *         print("Warning: gpt-3.5-turbo may change over time. Returning num tokens assuming gpt-3.5-turbo-0301.")
 *         return num_tokens_from_messages(messages, model="gpt-3.5-turbo-0301")
 *     elif model == "gpt-4":
 *         print("Warning: gpt-4 may change over time. Returning num tokens assuming gpt-4-0314.")
 *         return num_tokens_from_messages(messages, model="gpt-4-0314")
 *     elif model == "gpt-3.5-turbo-0301":
 *         tokens_per_message = 4  # every message follows <|start|>{role/name}\n{content}<|end|>\n
 *         tokens_per_name = -1  # if there's a name, the role is omitted
 *     elif model == "gpt-4-0314":
 *         tokens_per_message = 3
 *         tokens_per_name = 1
 *     else:
 *         raise NotImplementedError(f"""num_tokens_from_messages() is not implemented for model {model}. See https://github.com/openai/openai-python/blob/main/chatml.md for information on how messages are converted to tokens.""")
 *     num_tokens = 0
 *     for message in messages:
 *         num_tokens += tokens_per_message
 *         for key, value in message.items():
 *             num_tokens += len(encoding.encode(value))
 *             if key == "name":
 *                 num_tokens += tokens_per_name
 *     num_tokens += 3  # every reply is primed with <|start|>assistant<|message|>
 *     return num_tokens
 * }
 * </pre>
 *
 *
 * @author lzhpo
 */
@Slf4j
@UtilityClass
public class TokenUtils {

    private static final int REPLY_PRIMED_NUM = 3;
    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();
    private static final Map<String, ToIntFunction<String>> PER_MODEL_MAP = new HashMap<>(4);

    static {
        PER_MODEL_MAP.put("gpt-3.5-turbo", name -> 4 + (StringUtils.hasText(name) ? -1 : 0));
        PER_MODEL_MAP.put("gpt-3.5-turbo-0301", name -> 4 + (StringUtils.hasText(name) ? -1 : 0));
        PER_MODEL_MAP.put("gpt-4", name -> 3 + (StringUtils.hasText(name) ? 1 : 0));
        PER_MODEL_MAP.put("gpt-4-0314", name -> 3 + (StringUtils.hasText(name) ? 1 : 0));
    }

    /**
     * Get the {@link #REGISTRY}.
     *
     * @return {@link EncodingRegistry}
     */
    public static EncodingRegistry getRegistry() {
        return REGISTRY;
    }

    /**
     * Returns the encoding that is used for the given model type.
     *
     * @param modelType {@link ModelType}
     * @return the encoding
     */
    public static Encoding getEncoding(ModelType modelType) {
        return getRegistry().getEncodingForModel(modelType);
    }

    /**
     * Encodes the {@code content} into a list of token ids and returns the amount of tokens.
     *
     * @param modelType {@link ModelType}
     * @param content content
     * @return the tokens
     */
    public static Long tokens(ModelType modelType, String content) {
        Encoding encoding = getEncoding(modelType);
        return (long) encoding.countTokens(content);
    }

    /**
     * Encodes the {@code content} into a list of token ids and returns the amount of tokens.
     *
     * @param modelTypeName {@link ModelType} name
     * @param content content
     * @return the tokens
     */
    public static Long tokens(String modelTypeName, String content) {
        ModelType modelType = ModelType.fromName(modelTypeName)
                .orElseThrow(() -> new OpenAiException("Unknown model " + modelTypeName));
        return tokens(modelType, content);
    }

    /**
     * Encodes the {@code messages} into a list of token ids and returns the amount of tokens.
     *
     * @param model model
     * @param messages messages
     * @return tokens
     */
    public static Long tokens(String model, List<ChatCompletionMessage> messages) {
        Assert.hasText(model, "model cannot empty.");
        Assert.notEmpty(messages, "messages cannot empty.");
        return REPLY_PRIMED_NUM
                + messages.stream()
                        .map(message -> {
                            String name = message.getName();
                            ToIntFunction<String> handler = PER_MODEL_MAP.getOrDefault(model, x -> 0);
                            return handler.applyAsInt(name)
                                    + tokens(model, name)
                                    + tokens(model, message.getRole())
                                    + tokens(model, message.getContent());
                        })
                        .mapToLong(Long::longValue)
                        .sum();
    }
}
