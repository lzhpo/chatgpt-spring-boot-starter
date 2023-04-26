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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author lzhpo
 */
@Slf4j
public class InnerOpenAiKeyProvider implements OpenAiKeyProvider {

    private int controlNum;
    private static final Map<Integer, List<OpenAiKey>> OPEN_AI_KEYS_MAP = new HashMap<>();

    static {
        // spotless:off
        List<OpenAiKey> openAiKeys1 = new ArrayList<>();
        openAiKeys1.add(OpenAiKey.builder().key("sk-xxx1").weight(1.0).enabled(true).build());
        OPEN_AI_KEYS_MAP.put(1, openAiKeys1);

        List<OpenAiKey> openAiKeys2 = new ArrayList<>();
        openAiKeys2.add(OpenAiKey.builder().key("sk-xxx2").weight(2.0).enabled(true).build());
        OPEN_AI_KEYS_MAP.put(2, openAiKeys2);

        List<OpenAiKey> openAiKeys3 = new ArrayList<>();
        openAiKeys3.add(OpenAiKey.builder().key("sk-xxx").weight(666.0).enabled(true).build());
        OPEN_AI_KEYS_MAP.put(3, openAiKeys3);

        List<OpenAiKey> openAiKeys4 = new ArrayList<>();
        openAiKeys4.add(OpenAiKey.builder().key("sk-xxx").weight(666.0).enabled(true).build());
        OPEN_AI_KEYS_MAP.put(4, openAiKeys4);
        // spotless:on
    }

    @Override
    public List<OpenAiKey> get() {
        return OPEN_AI_KEYS_MAP.get(Math.min(++controlNum, 4));
    }
}
