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
public class OpenAiKey {

    /**
     * This api key.
     */
    private String key;

    /**
     * This api key weight.
     */
    private double weight;

    /**
     * Whether enable this api key.
     */
    @Builder.Default
    private boolean enabled = true;
}
