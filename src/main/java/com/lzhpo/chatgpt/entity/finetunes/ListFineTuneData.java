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

package com.lzhpo.chatgpt.entity.finetunes;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class ListFineTuneData {

    private String id;

    private String object;

    private String model;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("fine_tuned_model")
    private String fineTunedModel;

    private Hyperparams hyperparams;

    @JsonProperty("organizationId")
    private String organizationId;

    @JsonProperty("result_files")
    private List<FineTuneFiles> resultFiles;

    private String status;

    @JsonProperty("validation_files")
    private List<FineTuneFiles> validationFiles;

    @JsonProperty("training_files")
    private List<FineTuneFiles> trainingFiles;

    @JsonProperty("updated_at")
    private Long updatedAt;
}
