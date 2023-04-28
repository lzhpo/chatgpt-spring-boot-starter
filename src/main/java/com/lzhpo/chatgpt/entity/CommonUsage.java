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

package com.lzhpo.chatgpt.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class CommonUsage {

    /**
     * OpenAi calculate the input the number of tokens consumed.
     */
    @JsonProperty("prompt_tokens")
    private Long promptTokens;

    /**
     * OpenAi calculate the output the number of tokens consumed.
     */
    @JsonProperty("completion_tokens")
    private Long completionTokens;

    /**
     * OpenAi calculate the total number of tokens consumed by the input and output of this dialogue.
     * <p>totalTokens = {@link #promptTokens} + {@link #completionTokens}
     */
    @JsonProperty("total_tokens")
    private Long totalTokens;
}
