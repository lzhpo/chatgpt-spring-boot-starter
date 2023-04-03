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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.MediaType;

/**
 * @author lzhpo
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OpenAiConstant {

    /**
     * The image must be less than 4MB.
     */
    public static final long MAX_IMAGE_SIZE = 4 * 1024 * 1024L;

    /**
     * The image type must be png type.
     */
    public static final String EXPECTED_IMAGE_TYPE = "png";

    /**
     * The Authorization value prefix.
     */
    public static final String BEARER = "Bearer ";

    /**
     * The image/png.
     */
    public static final MediaType IMAGE_PNG = MediaType.get("image/png");

    /**
     * The application/json.
     */
    public static final MediaType APPLICATION_JSON = MediaType.get("application/json; charset=utf-8");
}
