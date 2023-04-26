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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import cn.hutool.core.lang.Console;
import com.lzhpo.chatgpt.entity.model.ListModelsResponse;
import com.lzhpo.chatgpt.entity.model.RetrieveModelResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author lzhpo
 */
@SpringBootTest
// @Import({InnerOpenAiKeyProvider.class})
class OpenAiKeyProviderTest {

    @Autowired
    private OpenAiClient openAiService;

    @MockBean
    private ServerEndpointExporter serverEndpointExporter;

    @Test
    void models() {
        ListModelsResponse modelsResponse = openAiService.models();
        assertNotNull(modelsResponse);
        List<RetrieveModelResponse> modelsResponseData = modelsResponse.getData();
        assertNotNull(modelsResponse);
        Console.log("Get the model size: {}", modelsResponseData.size());

        // test LFU cache
        for (int j = 0; j < Math.min(modelsResponseData.size(), 20); j++) {
            RetrieveModelResponse modelEntry = modelsResponseData.get(j);
            RetrieveModelResponse retrieveModelResponse = openAiService.retrieveModel(modelEntry.getId());
            assertNotNull(retrieveModelResponse);
            Console.log("modelId: {}", retrieveModelResponse.getId());
        }
    }
}
