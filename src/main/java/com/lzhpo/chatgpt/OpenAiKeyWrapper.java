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

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LFUCache;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.HashUtil;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * @author lzhpo
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class OpenAiKeyWrapper {

    private final OpenAiKeyProvider openAiKeyProvider;
    private final LFUCache<Long, WeightRandom<String>> randomLFUCache = CacheUtil.newLFUCache(0);

    /**
     * Wrap the {@link OpenAiKeyProvider#get()} result, in order to make api keys has weight random power.
     *
     * @return has weight random power api keys
     */
    public WeightRandom<String> wrap() {
        List<OpenAiKey> openAiKeys = openAiKeyProvider.get();
        Assert.notEmpty(openAiKeys, "The api keys is empty.");
        long cacheKey = HashUtil.murmur64(openAiKeys.toString().getBytes());
        return Optional.ofNullable(randomLFUCache.get(cacheKey)).orElseGet(() -> {
            log.debug("Not found openAiKeys in cache, will generate new one api key weight random.");
            Set<WeightRandom.WeightObj<String>> weightObjSet = openAiKeys.stream()
                    .filter(OpenAiKey::isEnabled)
                    .map(obj -> new WeightRandom.WeightObj<>(obj.getKey(), obj.getWeight()))
                    .collect(Collectors.toSet());
            WeightRandom<String> weightRandom = new WeightRandom<>(weightObjSet);
            randomLFUCache.put(cacheKey, weightRandom);
            return weightRandom;
        });
    }
}
