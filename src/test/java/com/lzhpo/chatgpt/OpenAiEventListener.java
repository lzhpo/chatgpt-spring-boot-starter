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

import com.lzhpo.chatgpt.exception.InvalidedKeyEvent;
import com.lzhpo.chatgpt.exception.NoAvailableKeyEvent;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author lzhpo
 */
@Slf4j
@Component
public class OpenAiEventListener {

    @EventListener
    public void processInvalidedKey(InvalidedKeyEvent event) {
        String invalidedApiKey = event.getInvalidedApiKey();
        String errorResponse = event.getErrorResponse();
        log.error("Processing invalidedApiKey={} event, errorResponse: {}", invalidedApiKey, errorResponse);
    }

    @EventListener
    public void processNoAvailableKey(NoAvailableKeyEvent event) {
        List<String> invalidedKeys = event.getInvalidedKeys();
        log.error("Processing noAvailableKey event, invalidedKeys={}", invalidedKeys);
    }
}
