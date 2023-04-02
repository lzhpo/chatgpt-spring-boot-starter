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

package com.lzhpo.chatgpt.entity.moderations;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.*;

/**
 * @author lzhpo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModerationRequest {

    /**
     * The input text to classify.
     */
    @NotEmpty
    private List<String> input;

    /**
     * Two content moderations models are available: text-moderation-stable and text-moderation-latest.
     *
     * <p>The default is text-moderation-latest which will be automatically upgraded over time.
     * This ensures you are always using our most accurate model.
     * If you use text-moderation-stable, we will provide advanced notice before updating the model.
     * Accuracy of text-moderation-stable may be slightly lower than for text-moderation-latest.
     */
    @NotBlank
    @Builder.Default
    private String model = "text-moderation-latest";
}
