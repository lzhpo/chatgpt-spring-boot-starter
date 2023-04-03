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

package com.lzhpo.chatgpt.entity.billing;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

/**
 * @author lzhpo
 */
@Data
public class SubscriptionResponse {

    private String object;

    @JsonProperty("has_payment_method")
    private Boolean has_payment_method;

    private Boolean canceled;

    @JsonProperty("canceled_at")
    private Long canceledAt;

    private Object delinquent;

    @JsonProperty("access_until")
    private Long accessUntil;

    @JsonProperty("soft_limit")
    private Long softLimit;

    @JsonProperty("hard_limit")
    private Long hardLimit;

    @JsonProperty("system_hard_limit")
    private Long systemHardLimit;

    @JsonProperty("soft_limit_usd")
    private BigDecimal softLimitUsd;

    @JsonProperty("hard_limit_usd")
    private BigDecimal hardLimitUsd;

    @JsonProperty("system_hard_limit_usd")
    private BigDecimal systemHardLimitUsd;

    private SubscriptionPlan plan;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("po_number")
    private String poNumber;

    @JsonProperty("billing_email")
    private String billingEmail;

    @JsonProperty("tax_ids")
    private List<String> taxIds;

    @JsonProperty("billing_address")
    private SubscriptionAddress billingAddress;

    @JsonProperty("business_address")
    private SubscriptionAddress businessAddress;
}
