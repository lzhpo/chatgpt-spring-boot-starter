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

package com.lzhpo.chatgpt.entity.audio;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateAudioRequest {

    /**
     * ID of the model to use.
     *
     * <p>Only whisper-1 is currently available.
     */
    @NotBlank
    private String model = "whisper-1";

    /**
     * An optional text to guide the model's style or continue a previous audio segment.
     *
     * <p>The <a href="https://platform.openai.com/docs/guides/speech-to-text/prompting">prompt</a> should match the audio language.
     */
    private String prompt;

    /**
     * The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.
     */
    @JsonProperty("response_format")
    private String responseFormat;

    /**
     * What sampling temperature to use, between 0 and 2.
     *
     * <p>Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     *
     * <p>We generally recommend altering this or top_p but not both.
     */
    @Min(0)
    @Max(2)
    private Number temperature;

    /**
     * The language of the input audio.
     *
     * <p>Supplying the input language in <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO-639-1</a> format will improve accuracy and latency.
     */
    private String language;
}
