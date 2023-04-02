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

package com.lzhpo.chatgpt.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class ModelPermission {

    private String id;

    private String object;

    private Long created;

    @JsonProperty("allow_create_engine")
    private String allowCreateEngine;

    @JsonProperty("allow_sampling")
    private String allowSampling;

    @JsonProperty("allow_logprobs")
    private String allowLogprobs;

    @JsonProperty("allow_search_indices")
    private String allowSearchIndices;

    @JsonProperty("allow_view")
    private String allowView;

    @JsonProperty("allow_fine_tuning")
    private String allowFineTuning;

    private String organization;

    private String group;

    @JsonProperty("is_blocking")
    private Boolean blocking;
}
