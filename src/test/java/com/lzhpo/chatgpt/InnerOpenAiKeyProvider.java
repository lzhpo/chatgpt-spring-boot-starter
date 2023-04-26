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
import java.util.List;

/**
 * @author lzhpo
 */
public class InnerOpenAiKeyProvider implements OpenAiKeyProvider {

    @Override
    public List<OpenAiKey> get() {
        // spotless:off
        List<OpenAiKey> openAiKeys = new ArrayList<>();
        openAiKeys.add(OpenAiKey.builder().key("sk-xxx1").weight(1.0).enabled(true).build());
        openAiKeys.add(OpenAiKey.builder().key("sk-xxx2").weight(2.0).enabled(false).build());
        openAiKeys.add(OpenAiKey.builder().key("sk-xxx2").weight(3.0).enabled(true).build());
        // spotless:on
        return openAiKeys;
    }
}
