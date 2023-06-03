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

package com.lzhpo.chatgpt.exception;

import cn.hutool.core.collection.ListUtil;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author lzhpo
 */
@SuppressWarnings({"squid:S2386"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenAiErrorCode {

    public static final String INVALID_API_KEY = "invalid_api_key";
    public static final String ACCOUNT_DEACTIVATED = "account_deactivated";
    public static final String INSUFFICIENT_QUOTA = "insufficient_quota";
    public static final String ACCESS_TERMINATED = "access_terminated";

    public static final List<Integer> ROTATION_HTTP_CODES = ListUtil.of(401, 429);
    public static final List<String> ROTATION_ERROR_TYPES_OR_CODES =
            ListUtil.of(INVALID_API_KEY, ACCOUNT_DEACTIVATED, INSUFFICIENT_QUOTA, ACCESS_TERMINATED);
}
