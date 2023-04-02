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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class ModerationCategoryScore {

    private BigDecimal hate;

    @JsonProperty("hate/threatening")
    private BigDecimal threatening;

    @JsonProperty("self-harm")
    private BigDecimal selfHarm;

    private BigDecimal sexual;

    @JsonProperty("sexual/minors")
    private BigDecimal minors;

    private BigDecimal violence;

    @JsonProperty("violence/graphic")
    private BigDecimal graphic;
}
