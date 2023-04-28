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

import static com.lzhpo.chatgpt.OpenAiConstant.*;
import static com.lzhpo.chatgpt.OpenAiUrl.*;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.http.Header;
import com.lzhpo.chatgpt.entity.audio.CreateAudioRequest;
import com.lzhpo.chatgpt.entity.audio.CreateAudioResponse;
import com.lzhpo.chatgpt.entity.billing.CreditGrantsResponse;
import com.lzhpo.chatgpt.entity.billing.SubscriptionResponse;
import com.lzhpo.chatgpt.entity.billing.UsageResponse;
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
import com.lzhpo.chatgpt.entity.image.CreateImageRequest;
import com.lzhpo.chatgpt.entity.image.CreateImageResponse;
import com.lzhpo.chatgpt.entity.image.CreateImageVariationRequest;
import com.lzhpo.chatgpt.entity.model.ListModelsResponse;
import com.lzhpo.chatgpt.entity.model.RetrieveModelResponse;
import com.lzhpo.chatgpt.entity.moderations.ModerationRequest;
import com.lzhpo.chatgpt.entity.moderations.ModerationResponse;
import com.lzhpo.chatgpt.entity.users.UserResponse;
import com.lzhpo.chatgpt.sse.Listener;
import com.lzhpo.chatgpt.utils.JsonUtils;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriTemplateHandler;

/**
 * @author lzhpo
 */
@Slf4j
@Validated
@RequiredArgsConstructor
public class DefaultOpenAiClient implements OpenAiClient {

    private final OkHttpClient okHttpClient;
    private final OpenAiProperties openAiProperties;
    private final UriTemplateHandler uriTemplateHandler;
    private final WeightRandom<String> apiKeyWeightRandom;

    @Override
    public ModerationResponse moderations(ModerationRequest request) {
        return execute(MODERATIONS, createRequestBody(request), ModerationResponse.class);
    }

    @Override
    public CompletionResponse completions(CompletionRequest request) {
        return execute(COMPLETIONS, createRequestBody(request), CompletionResponse.class);
    }

    @Override
    public void streamCompletions(CompletionRequest request, Listener listener) {
        request.setStream(true);
        Request clientRequest = createRequest(COMPLETIONS, createRequestBody(request));
        RealEventSource realEventSource = new RealEventSource(clientRequest, (EventSourceListener) listener);
        realEventSource.connect(okHttpClient);
    }

    @Override
    public EditResponse edits(EditRequest request) {
        return execute(EDITS, createRequestBody(request), EditResponse.class);
    }

    @Override
    public ChatCompletionResponse chatCompletions(ChatCompletionRequest request) {
        return execute(CHAT_COMPLETIONS, createRequestBody(request), ChatCompletionResponse.class);
    }

    @Override
    public void streamChatCompletions(ChatCompletionRequest request, Listener listener) {
        request.setStream(true);
        Request clientRequest = createRequest(CHAT_COMPLETIONS, createRequestBody(request));
        RealEventSource realEventSource = new RealEventSource(clientRequest, (EventSourceListener) listener);
        realEventSource.connect(okHttpClient);
    }

    @Override
    public ListModelsResponse models() {
        return execute(LIST_MODELS, null, ListModelsResponse.class);
    }

    @Override
    public RetrieveModelResponse retrieveModel(String modelId) {
        return execute(RETRIEVE_MODEL, null, RetrieveModelResponse.class, modelId);
    }

    @Override
    public EmbeddingResponse embeddings(EmbeddingRequest request) {
        return execute(EMBEDDINGS, createRequestBody(request), EmbeddingResponse.class);
    }

    @Override
    public ListFileResponse listFiles() {
        return execute(LIST_FILES, null, ListFileResponse.class);
    }

    @Override
    @SneakyThrows
    public UploadFileResponse uploadFile(Resource fileResource, String purpose) {
        byte[] bytes = IoUtil.readBytes(fileResource.getInputStream());
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("purpose", purpose)
                .addFormDataPart("file", fileResource.getFilename(), RequestBody.create(bytes, IMAGE_PNG))
                .build();
        return execute(UPLOAD_FILE, multipartBody, UploadFileResponse.class);
    }

    @Override
    public DeleteFileResponse deleteFile(String fileId) {
        return execute(DELETE_FILE, null, DeleteFileResponse.class, fileId);
    }

    @Override
    public RetrieveFileResponse retrieveFile(String fileId) {
        return execute(RETRIEVE_FILE, null, RetrieveFileResponse.class, fileId);
    }

    @Override
    public CreateFineTuneResponse createFineTune(CreateFineTuneRequest request) {
        return execute(CREATE_FINE_TUNE, createRequestBody(request), CreateFineTuneResponse.class);
    }

    @Override
    public ListFineTuneResponse listFineTunes() {
        return execute(LIST_FINE_TUNE, null, ListFineTuneResponse.class);
    }

    @Override
    public RetrieveFineTuneResponse retrieveFineTunes(String fineTuneId) {
        return execute(RETRIEVE_FINE_TUNE, null, RetrieveFineTuneResponse.class, fineTuneId);
    }

    @Override
    public CancelFineTuneResponse cancelFineTune(String fineTuneId) {
        return execute(CANCEL_FINE_TUNE, RequestBody.create("", null), CancelFineTuneResponse.class, fineTuneId);
    }

    @Override
    public ListFineTuneEventResponse listFineTuneEvents(String fineTuneId) {
        return execute(LIST_FINE_TUNE_EVENTS, null, ListFineTuneEventResponse.class, fineTuneId);
    }

    @Override
    public DeleteFineTuneModelResponse deleteFineTuneModel(String model) {
        return execute(DELETE_FINE_TUNE_EVENTS, null, DeleteFineTuneModelResponse.class, model);
    }

    @Override
    public CreateAudioResponse createTranscription(Resource fileResource, CreateAudioRequest request) {
        MultipartBody multipartBody = createAudioBody(fileResource, request);
        return execute(CREATE_TRANSCRIPTION, multipartBody, CreateAudioResponse.class);
    }

    @Override
    public CreateAudioResponse createTranslation(Resource fileResource, CreateAudioRequest request) {
        MultipartBody multipartBody = createAudioBody(fileResource, request);
        return execute(CREATE_TRANSLATION, multipartBody, CreateAudioResponse.class);
    }

    @Override
    public CreateImageResponse createImage(CreateImageRequest request) {
        return execute(CREATE_IMAGE, createRequestBody(request), CreateImageResponse.class);
    }

    @Override
    @SneakyThrows
    public CreateImageResponse createImageEdit(Resource image, Resource mask, CreateImageRequest request) {
        boolean imageIsPng = FileNameUtil.isType(image.getFilename(), EXPECTED_IMAGE_TYPE);
        boolean maskIsPng = FileNameUtil.isType(mask.getFilename(), EXPECTED_IMAGE_TYPE);
        Assert.isTrue(imageIsPng, "The image must png type.");
        Assert.isTrue(maskIsPng, "The mask must png type.");

        Assert.isTrue(image.contentLength() < MAX_IMAGE_SIZE, "The image must less than 4MB.");
        Assert.isTrue(mask.contentLength() < MAX_IMAGE_SIZE, "The mask must less than 4MB.");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("image", image.getFilename(), createResourceBody(image));
        builder.addFormDataPart("mask", mask.getFilename(), createResourceBody(mask));

        PropertyMapper mapper = buildImageForm(request, builder);
        mapper.from(request.getPrompt()).to(prompt -> builder.addFormDataPart("prompt", prompt));
        return execute(CREATE_IMAGE_EDIT, builder.build(), CreateImageResponse.class);
    }

    @Override
    @SneakyThrows
    public CreateImageResponse createImageVariation(Resource image, CreateImageVariationRequest request) {
        boolean imageIsPng = FileNameUtil.isType(image.getFilename(), EXPECTED_IMAGE_TYPE);
        Assert.isTrue(imageIsPng, "The image must png type.");
        Assert.isTrue(image.contentLength() < MAX_IMAGE_SIZE, "The image must less than 4MB.");

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("image", image.getFilename(), createResourceBody(image));

        buildImageForm(request, builder);
        return execute(CREATE_IMAGE_VARIATION, builder.build(), CreateImageResponse.class);
    }

    @Override
    public CreditGrantsResponse billingCreditGrants() {
        return execute(BILLING_CREDIT_GRANTS, null, CreditGrantsResponse.class);
    }

    @Override
    public UserResponse users(String organizationId) {
        return execute(USERS, null, UserResponse.class, organizationId);
    }

    @Override
    public SubscriptionResponse billingSubscription() {
        return execute(BILLING_SUBSCRIPTION, null, SubscriptionResponse.class);
    }

    @Override
    public UsageResponse billingUsage(String startDate, String endDate) {
        return execute(BILLING_USAGE, null, UsageResponse.class, startDate, endDate);
    }

    @SneakyThrows
    private <S> S execute(OpenAiUrl openAiUrl, RequestBody requestBody, Class<S> responseType, Object... uriVariables) {
        Request clientRequest = createRequest(openAiUrl, requestBody, uriVariables);
        @Cleanup Response response = okHttpClient.newCall(clientRequest).execute();

        ResponseBody body = response.body();
        Assert.notNull(body, "Resolve response body failed.");
        String responseBody = body.string();

        int code = response.code();
        HttpStatus httpStatus = HttpStatus.resolve(code);

        Assert.notNull(httpStatus, () -> {
            log.error("Unknown http status code: {}", code);
            log.error("Request message: {}", clientRequest);
            throw new OpenAiException(responseBody);
        });

        Assert.isTrue(httpStatus.is2xxSuccessful(), () -> {
            log.error("Response code: {}", code);
            log.error("Request message: {}", clientRequest);
            throw new OpenAiException(responseBody);
        });

        return JsonUtils.parse(responseBody, responseType);
    }

    private Request createRequest(OpenAiUrl openAiUrl, RequestBody requestBody, Object... uriVariables) {
        Map<OpenAiUrl, String> configUrls = openAiProperties.getUrls();
        String requestUrl = configUrls.get(openAiUrl);
        if (!StringUtils.hasText(requestUrl)) {
            requestUrl = openAiProperties.getDomain() + openAiUrl.getSuffix();
        }
        URI requestURI = uriTemplateHandler.expand(requestUrl, uriVariables);
        return new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.get(requestURI)))
                .header(Header.AUTHORIZATION.name(), BEARER.concat(apiKeyWeightRandom.next()))
                .method(openAiUrl.getMethod(), requestBody)
                .build();
    }

    private RequestBody createRequestBody(Object request) {
        return RequestBody.create(JsonUtils.toJsonString(request), APPLICATION_JSON);
    }

    @SneakyThrows
    private RequestBody createResourceBody(Resource resource) {
        return RequestBody.create(IoUtil.readBytes(resource.getInputStream()), IMAGE_PNG);
    }

    private MultipartBody createAudioBody(Resource fileResource, CreateAudioRequest request) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file", fileResource.getFilename(), createResourceBody(fileResource));

        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(request.getModel()).to(model -> builder.addFormDataPart("model", model));
        mapper.from(request.getPrompt()).to(prompt -> builder.addFormDataPart("prompt", prompt));
        mapper.from(request.getResponseFormat()).to(format -> builder.addFormDataPart("response_format", format));
        mapper.from(request.getTemperature()).to(obj -> builder.addFormDataPart("temperature", obj.toString()));
        mapper.from(request.getLanguage()).to(language -> builder.addFormDataPart("language", language));
        return builder.build();
    }

    private PropertyMapper buildImageForm(CreateImageVariationRequest request, MultipartBody.Builder builder) {
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(request.getN()).to(n -> builder.addFormDataPart("n", n.toString()));
        mapper.from(request.getSize()).to(size -> builder.addFormDataPart("size", size.getValue()));
        mapper.from(request.getResponseFormat()).to(obj -> builder.addFormDataPart("response_format", obj.getValue()));
        mapper.from(request.getUser()).to(user -> builder.addFormDataPart("user", user));
        return mapper;
    }
}
