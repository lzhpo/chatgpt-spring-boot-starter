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

package com.lzhpo.chatgpt.entity.completions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lzhpo.chatgpt.entity.CommonConfig;
import java.util.Map;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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
public class CompletionRequest extends CommonConfig {

    /**
     * ID of the model to use.
     *
     * <p>You can use the <a href="https://platform.openai.com/docs/api-reference/models/list">List models</a> API to see all of your available models,
     * or see our <a href="https://platform.openai.com/docs/models/overview">Model overview</a> for descriptions of them.
     * <a href="https://platform.openai.com/docs/models/model-endpoint-compatibility">Model endpoint compatibility</a>
     */
    @NotBlank
    @Builder.Default
    private String model = "text-davinci-003";

    /**
     * The prompt(s) to generate completions for, encoded as a string, array of strings, array of tokens, or array of token arrays.
     *
     * <p>Note that <|endoftext|> is the document separator that the model sees during training, so if a prompt is not specified the model will generate as if from the beginning of a new document.
     */
    private String prompt;

    /**
     * The suffix that comes after a completion of inserted text.
     */
    private String suffix;

    /**
     * The maximum number of <a href="https://platform.openai.com/tokenizer">tokens</a> to generate in the completion.
     *
     * <p>The token count of your prompt plus max_tokens cannot exceed the model's context length.
     * Most models have a context length of 2048 tokens (except for the newest models, which support 4096).
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /**
     * If set, partial message deltas will be sent, like in ChatGPT.
     * Tokens will be sent as data-only <a href="https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format">server-sent events</a> as they become available, with the stream terminated by a {@code data: [DONE]} message.
     *
     * <p><a href="https://github.com/openai/openai-cookbook/blob/main/examples/How_to_stream_completions.ipynb">See the OpenAI Cookbook for example code.</a>
     */
    private Boolean stream;

    /**
     * Include the log probabilities on the logprobs most likely tokens, as well the chosen tokens.
     * For example, if logprobs is 5, the API will return a list of the 5 most likely tokens.
     * The API will always return the logprob of the sampled token, so there may be up to logprobs+1 elements in the response.
     *
     * <p>The maximum value for logprobs is 5. If you need more than this, please contact us through our <a href="https://help.openai.com/">Help center</a> and describe your use case.
     */
    private Integer logprobs;

    /**
     * Echo back the prompt in addition to the completion.
     */
    private String echo;

    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     * The returned text will not contain the stop sequence.
     */
    private String stop;

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
     * Generates best_of completions server-side and returns the "best" (the one with the highest log probability per token). Results cannot be streamed.
     *
     * <p>When used with n, best_of controls the number of candidate completions and n specifies how many to return – best_of must be greater than n.
     *
     * <p>Note: Because this parameter generates many completions, it can quickly consume your token quota.
     * Use carefully and ensure that you have reasonable settings for max_tokens and stop.
     */
    @JsonProperty("best_of")
    private Integer bestOf;

    /**
     * Modify the likelihood of specified tokens appearing in the completion.
     *
     * <p>Accepts a json object that maps tokens (specified by their token ID in the GPT tokenizer) to an associated bias value from -100 to 100.
     * You can use this <a href="https://platform.openai.com/tokenizer?view=bpe">tokenizer</a> tool (which works for both GPT-2 and GPT-3) to convert text to token IDs.
     * Mathematically, the bias is added to the logits generated by the model prior to sampling.
     * The exact effect will vary per model, but values between -1 and 1 should decrease or increase likelihood of selection;
     * values like -100 or 100 should result in a ban or exclusive selection of the relevant token.
     *
     * <p>As an example, you can pass {"50256": -100} to prevent the <|endoftext|> token from being generated.
     */
    @JsonProperty("logit_bias")
    private Map<Object, Object> logitBias;

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     *
     * <p><a href="https://platform.openai.com/docs/guides/safety-best-practices/end-user-ids">Learn more.</a>
     */
    private String user;
}
