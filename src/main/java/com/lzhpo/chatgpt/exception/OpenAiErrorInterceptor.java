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

import static com.lzhpo.chatgpt.OpenAiConstant.BEARER;
import static com.lzhpo.chatgpt.exception.OpenAiErrorCode.ROTATION_ERROR_TYPES_OR_CODES;
import static com.lzhpo.chatgpt.exception.OpenAiErrorCode.ROTATION_HTTP_CODES;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.Header;
import com.lzhpo.chatgpt.apikey.OpenAiKeyWrapper;
import com.lzhpo.chatgpt.utils.JsonUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author lzhpo
 */
@Slf4j
@RequiredArgsConstructor
public class OpenAiErrorInterceptor implements Interceptor {

    private final OpenAiKeyWrapper openAiKeyWrapper;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        int code = response.code();
        String authorization = request.header(Header.AUTHORIZATION.name());
        String apiKey = StrUtil.replace(authorization, BEARER, StrUtil.EMPTY);

        if (ROTATION_HTTP_CODES.contains(code) && StringUtils.hasText(apiKey)) {
            ResponseBody responseBody = response.body();
            Assert.notNull(responseBody, "Resolve response body failed.");
            BufferedSource responseBodySource = responseBody.source();
            responseBodySource.request(Long.MAX_VALUE);
            Buffer responseBodyBuffer = responseBodySource.getBuffer();
            String responseBodyStr = responseBodyBuffer.clone().readString(StandardCharsets.UTF_8);

            OpenAiError openAiError = JsonUtils.parse(responseBodyStr, OpenAiError.class);
            Optional.ofNullable(openAiError)
                    .map(OpenAiError::getError)
                    .filter(openAiErrorDetail -> {
                        String errorType = openAiErrorDetail.getType();
                        String errorCode = openAiErrorDetail.getCode();
                        return ROTATION_ERROR_TYPES_OR_CODES.contains(errorType)
                                || ROTATION_ERROR_TYPES_OR_CODES.contains(errorCode);
                    })
                    .ifPresent(errorCode -> openAiKeyWrapper.invalidKey(apiKey));
            SpringUtil.publishEvent(new InvalidedKeyEvent(this, apiKey, responseBodyStr));
        }

        return response;
    }
}
