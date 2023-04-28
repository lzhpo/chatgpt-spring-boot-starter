/*
 * Copyright 2023 luna
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

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Console;
import com.luna.common.net.sse.Event;
import com.luna.common.net.sse.SseResponse;
import com.lzhpo.chatgpt.entity.audio.CreateAudioRequest;
import com.lzhpo.chatgpt.entity.audio.CreateAudioResponse;
import com.lzhpo.chatgpt.entity.billing.CreditGrantsResponse;
import com.lzhpo.chatgpt.entity.billing.SubscriptionResponse;
import com.lzhpo.chatgpt.entity.billing.UsageResponse;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionMessage;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionRequest;
import com.lzhpo.chatgpt.entity.chat.ChatCompletionResponse;
import com.lzhpo.chatgpt.entity.completions.CompletionRequest;
import com.lzhpo.chatgpt.entity.completions.CompletionResponse;
import com.lzhpo.chatgpt.entity.edit.EditRequest;
import com.lzhpo.chatgpt.entity.edit.EditResponse;
import com.lzhpo.chatgpt.entity.embeddings.EmbeddingRequest;
import com.lzhpo.chatgpt.entity.embeddings.EmbeddingResponse;
import com.lzhpo.chatgpt.entity.files.DeleteFileResponse;
import com.lzhpo.chatgpt.entity.files.ListFileResponse;
import com.lzhpo.chatgpt.entity.files.RetrieveFileResponse;
import com.lzhpo.chatgpt.entity.files.UploadFileResponse;
import com.lzhpo.chatgpt.entity.finetunes.*;
import com.lzhpo.chatgpt.entity.image.*;
import com.lzhpo.chatgpt.entity.model.ListModelsResponse;
import com.lzhpo.chatgpt.entity.model.RetrieveModelResponse;
import com.lzhpo.chatgpt.entity.moderations.ModerationRequest;
import com.lzhpo.chatgpt.entity.moderations.ModerationResponse;
import com.lzhpo.chatgpt.entity.users.UserResponse;
import com.lzhpo.chatgpt.sse.CountDownLatchEventSourceListener;
import com.lzhpo.chatgpt.sse.CustomDownLatchEventFutureCallback;
import com.lzhpo.chatgpt.utils.JsonUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author lzhpo
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HttpOpenAiClientTest {

    @Autowired
    private HttpOpenAiClient openAiService;

    @MockBean
    private ServerEndpointExporter serverEndpointExporter;

    @Test
    @Order(1)
    void moderations() {
        ModerationRequest request = new ModerationRequest();
        request.setInput(ListUtil.of("I want to kill them."));

        ModerationResponse response = openAiService.moderations(request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(2)
    void completions() {
        CompletionRequest request = new CompletionRequest();
        request.setModel("text-davinci-003");
        request.setPrompt("Say this is a test");
        request.setMaxTokens(7);
        request.setTemperature(0);

        CompletionResponse response = openAiService.completions(request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(3)
    void streamCompletions() throws InterruptedException {
        CompletionRequest request = new CompletionRequest();
        request.setStream(true);
        request.setModel("text-davinci-003");
        request.setPrompt("Say this is a test");
        request.setMaxTokens(7);
        request.setTemperature(0);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CustomDownLatchEventFutureCallback<Event, SseResponse> futureCallback = new CustomDownLatchEventFutureCallback<>(countDownLatch);
        assertDoesNotThrow(() -> openAiService.streamCompletions(request, futureCallback));
        countDownLatch.await();
    }

    @Test
    @Order(4)
    void edits() {
        EditRequest request = new EditRequest();
        request.setModel("text-davinci-edit-001");
        request.setInput("What day of the wek is it?");
        request.setInstruction("Fix the spelling mistakes");

        EditResponse response = openAiService.edits(request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(5)
    void chatCompletions() {
        List<ChatCompletionMessage> messages = new ArrayList<>();
        ChatCompletionMessage message = new ChatCompletionMessage();
        message.setRole("user");
        message.setContent("Hello");
        messages.add(message);

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(messages);

        ChatCompletionResponse response = openAiService.chatCompletions(request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(6)
    void streamChatCompletions() throws InterruptedException {
        List<ChatCompletionMessage> messages = new ArrayList<>();
        ChatCompletionMessage message = new ChatCompletionMessage();
        message.setRole("user");
        message.setContent("Hello");
        messages.add(message);

        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setStream(true);
        request.setModel("gpt-3.5-turbo");
        request.setMessages(messages);

        final CountDownLatch countDownLatch = new CountDownLatch(5);
        CustomDownLatchEventFutureCallback<Event, SseResponse> eventSourceListener = new CustomDownLatchEventFutureCallback<>(countDownLatch);
        assertDoesNotThrow(() -> openAiService.streamChatCompletions(request, eventSourceListener));
        countDownLatch.await();
    }

    @Test
    @Order(7)
    void models() {
        ListModelsResponse response = openAiService.models();

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(8)
    void retrieveModel() {
        RetrieveModelResponse response = openAiService.retrieveModel("babbage");

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(9)
    void embeddings() {
        EmbeddingRequest request = new EmbeddingRequest();
        request.setModel("text-embedding-ada-002");
        request.setInput(ListUtil.of("The food was delicious and the waiter..."));
        EmbeddingResponse response = openAiService.embeddings(request);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(10)
    void uploadFile() {
        final String path = "C:\\Users\\lzhpo\\Desktop\\xxx.txt";
        FileSystemResource fileResource = new FileSystemResource(path);

        UploadFileResponse response = openAiService.uploadFile(fileResource, "fine-tune");
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(11)
    void listFiles() {
        ListFileResponse response = openAiService.listFiles();

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(12)
    void retrieveFile() {
        final String fileId = "file-xxx";
        RetrieveFileResponse response = openAiService.retrieveFile(fileId);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(13)
    void deleteFile() {
        final String fileId = "file-xxx";
        DeleteFileResponse response = openAiService.deleteFile(fileId);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(14)
    void createFineTune() {
        CreateFineTuneRequest request = new CreateFineTuneRequest();
        request.setTrainingFile("file-xxx");

        CreateFineTuneResponse response = openAiService.createFineTune(request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(15)
    void listFineTunes() {
        ListFineTuneResponse response = openAiService.listFineTunes();

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(16)
    void retrieveFineTunes() {
        final String fineTuneId = "ft-xxx";
        RetrieveFineTuneResponse response = openAiService.retrieveFineTunes(fineTuneId);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(17)
    void cancelFineTune() {
        final String fineTuneId = "ft-xxx";
        CancelFineTuneResponse response = openAiService.cancelFineTune(fineTuneId);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(18)
    void listFineTuneEvents() {
        final String fineTuneId = "ft-xxx";
        ListFineTuneEventResponse response = openAiService.listFineTuneEvents(fineTuneId);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(19)
    void deleteFineTuneModel() {
        final String modelId = "curie:ft-xxx";
        DeleteFineTuneModelResponse response = openAiService.deleteFineTuneModel(modelId);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(20)
    void createTranscription() {
        final String path = "C:\\Users\\lzhpo\\Downloads\\xxx.mp3";
        FileSystemResource fileResource = new FileSystemResource(path);
        CreateAudioRequest request = new CreateAudioRequest();
        request.setModel("whisper-1");

        CreateAudioResponse response = openAiService.createTranscription(fileResource, request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(21)
    void createTranslation() {
        final String path = "C:\\Users\\lzhpo\\Downloads\\xxx.mp3";
        FileSystemResource fileResource = new FileSystemResource(path);
        CreateAudioRequest request = new CreateAudioRequest();
        request.setModel("whisper-1");

        CreateAudioResponse response = openAiService.createTranslation(fileResource, request);
        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(22)
    void createImage() {
        CreateImageRequest request = new CreateImageRequest();
        request.setPrompt("A cute baby sea otter.");
        request.setN(2);
        request.setSize(CreateImageSize.X_512_512);
        request.setResponseFormat(CreateImageResponseFormat.URL);
        CreateImageResponse response = openAiService.createImage(request);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(23)
    void createImageEdit() {
        final String imagePath = "C:\\Users\\lzhpo\\Downloads\\img-xxx.png";
        FileSystemResource imageResource = new FileSystemResource(imagePath);

        final String markPath = "C:\\Users\\lzhpo\\Downloads\\img-xxx.png";
        FileSystemResource markResource = new FileSystemResource(markPath);

        CreateImageRequest request = new CreateImageRequest();
        request.setPrompt("A cute baby sea otter.");
        request.setN(2);
        request.setSize(CreateImageSize.X_512_512);
        request.setResponseFormat(CreateImageResponseFormat.URL);
        CreateImageResponse response = openAiService.createImageEdit(imageResource, markResource, request);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(24)
    void createImageVariation() {
        final String imagePath = "C:\\Users\\lzhpo\\Downloads\\img-xxx.png";
        FileSystemResource imageResource = new FileSystemResource(imagePath);

        CreateImageVariationRequest request = new CreateImageVariationRequest();
        request.setN(2);
        request.setSize(CreateImageSize.X_512_512);
        request.setResponseFormat(CreateImageResponseFormat.URL);
        CreateImageResponse response = openAiService.createImageVariation(imageResource, request);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(25)
    void billingCreditGrants() {
        CreditGrantsResponse response = openAiService.billingCreditGrants();

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(26)
    void users() {
        UserResponse response = openAiService.users("org-xxx");

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(27)
    void billingSubscription() {
        SubscriptionResponse response = openAiService.billingSubscription();

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }

    @Test
    @Order(28)
    void billingUsage() {
        Date nowDate = new Date();
        String startDate = DateUtil.format(DateUtil.offsetDay(nowDate, -100), DatePattern.NORM_DATE_PATTERN);
        String endDate = DateUtil.format(nowDate, DatePattern.NORM_DATE_PATTERN);
        UsageResponse response = openAiService.billingUsage(startDate, endDate);

        assertNotNull(response);
        Console.log(JsonUtils.toJsonPrettyString(response));
    }
}
