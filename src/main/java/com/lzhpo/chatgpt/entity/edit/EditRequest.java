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

package com.lzhpo.chatgpt.entity.edit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lzhpo.chatgpt.entity.CommonConfig;
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
public class EditRequest extends CommonConfig {

    /**
     * ID of the model to use.
     *
     * <p>You can use the text-davinci-edit-001 or code-davinci-edit-001 model with this endpoint.
     */
    @NotBlank
    @Builder.Default
    private String model = "text-davinci-edit-001";

    /**
     * The input text to use as a starting point for the edit.
     */
    @NotBlank
    private String input;

    /**
     * The instruction that tells the model how to edit the prompt.
     */
    @NotBlank
    private String instruction;
}
