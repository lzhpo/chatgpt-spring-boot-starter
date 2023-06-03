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

package com.lzhpo.chatgpt.apikey;

import java.util.List;

/**
 * @author lzhpo
 */
public interface OpenAiKeyProvider {

    /**
     * Get the {@link OpenAiKey}.
     *
     * <p>Notes: if you get the api keys from DB, also can add cache to improve speed.
     *
     * @return list of {@link OpenAiKey}
     */
    List<OpenAiKey> get();
}
