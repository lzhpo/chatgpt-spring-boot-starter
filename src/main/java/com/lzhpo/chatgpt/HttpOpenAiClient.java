package com.lzhpo.chatgpt;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.WeightRandom;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.luna.common.file.FileTools;
import com.luna.common.net.HttpUtils;
import com.luna.common.net.HttpUtilsConstant;
import com.luna.common.net.async.CustomAbstacktFutureCallback;
import com.luna.common.net.async.CustomAsyncHttpResponse;
import com.luna.common.net.async.CustomSseAsyncConsumer;
import com.luna.common.net.high.AsyncHttpUtils;
import com.luna.common.net.sse.Event;
import com.luna.common.net.sse.SseResponse;
import com.luna.common.text.CharsetUtil;
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
import com.lzhpo.chatgpt.sse.*;
import com.lzhpo.chatgpt.utils.JsonUtils;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityProducer;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriTemplateHandler;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.lzhpo.chatgpt.OpenAiConstant.BEARER;
import static com.lzhpo.chatgpt.OpenAiUrl.*;

/**
 * @author luna
 * @description
 * @date 2023/4/22
 */
@Slf4j
@Validated
@RequiredArgsConstructor
public class HttpOpenAiClient implements OpenAiClient {

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
        URI uri = getUri(COMPLETIONS, request);
//        doRequestAsync(uri, listener);

        execute(COMPLETIONS, createRequestBody(request), CompletionResponse.class);
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
    public void streamChatCompletions(ChatCompletionRequest request, Listener eventListener) {
        request.setStream(true);
        CustomAbstacktFutureCallback customAbstacktFutureCallback = new CustomDownLatchEventFutureCallback(new CountDownLatch(1));
        doRequestAsync(CHAT_COMPLETIONS, JsonUtils.toJsonString(request), (CustomDownLatchEventFutureCallback<Event>) eventListener, customAbstacktFutureCallback);
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
        HashMap<String, String> map = Maps.newHashMap();
        map.put("purpose", purpose);
        map.put("file", fileResource.getFile().getAbsolutePath());
        HttpEntity requestBody = createRequestBody(map);
        return execute(UPLOAD_FILE, requestBody, UploadFileResponse.class);
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
        return execute(CANCEL_FINE_TUNE, createRequestBody(null), CancelFineTuneResponse.class, fineTuneId);
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
        HttpEntity audioBody = createAudioBody(fileResource, request);
        return execute(CREATE_TRANSCRIPTION, audioBody, CreateAudioResponse.class);
    }

    @Override
    public CreateAudioResponse createTranslation(Resource fileResource, CreateAudioRequest request) {
        HttpEntity multipartBody  = createAudioBody(fileResource, request);
        return execute(CREATE_TRANSLATION, multipartBody, CreateAudioResponse.class);
    }

    @Override
    public CreateImageResponse createImage(CreateImageRequest request) {
        return execute(CREATE_IMAGE, createRequestBody(request), CreateImageResponse.class);
    }

    @Override
    public CreateImageResponse createImageEdit(Resource image, Resource mask, CreateImageRequest request) {
        return null;
    }

    @Override
    public CreateImageResponse createImageVariation(Resource image, CreateImageVariationRequest request) {
        return null;
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
    private <S> S execute(OpenAiUrl openAiUrl, HttpEntity requestBody, Class<S> responseType, Object... uriVariables) {
        String request = doRequest(openAiUrl, requestBody, uriVariables);
        Assert.notNull(request, "Resolve response body failed.");
        return JsonUtils.parse(request, responseType);
    }

    private String doRequest(OpenAiUrl openAiUrl, HttpEntity httpEntity, Object... uriVariables) {
        URI requestUri = getUri(openAiUrl, uriVariables);

        Map<String, String> header = Maps.newHashMap();
        header.put(HttpHeaders.AUTHORIZATION, BEARER.concat(apiKeyWeightRandom.next()));

        String result;
        if (HttpMethod.POST.toString().equals(openAiUrl.getMethod())) {
            header.put(HttpHeaders.CONTENT_TYPE, HttpUtilsConstant.JSON);
            result = HttpUtils.doPost(openAiProperties.getDomain(), requestUri.getPath(), header, new HashMap<>(), httpEntity, new BasicHttpClientResponseHandler());
        } else if (HttpMethod.GET.toString().equals(openAiUrl.getMethod())) {
            result = HttpUtils.doGet(openAiProperties.getDomain(), requestUri.getPath(), header, new HashMap<>(), new BasicHttpClientResponseHandler());
        } else {
            throw new OpenAiException("不支持的请求方式");
        }

        return result;
    }

    public void doRequestAsync(OpenAiUrl openAiUrl, String body, CustomAbstacktFutureCallback<Event> eventCallBack, CustomAbstacktFutureCallback<SseResponse> resultCallback) {
        URI requestUri = getUri(openAiUrl);
        Map<String, String> header = Maps.newHashMap();
        header.put(HttpHeaders.AUTHORIZATION, BEARER.concat(apiKeyWeightRandom.next()));

        StringAsyncEntityProducer bodyProducer = new StringAsyncEntityProducer(body);
        AsyncRequestProducer producer = AsyncHttpUtils.getProducer("http://localhost:6060", "/stream-sse-mvc", header, new HashMap<>(), bodyProducer, HttpMethod.GET.toString());

        CustomEventSourceListener customEventSourceListener = new CustomEventSourceListener(eventCallBack, resultCallback);
        // 事件处理器
        CustomSseAsyncConsumer customSseAsyncConsumer = new CustomSseAsyncConsumer(customEventSourceListener);
        SseResponse sseResponse = AsyncHttpUtils.doAsyncRequest(producer, customSseAsyncConsumer, null);
    }

    @NotNull
    private URI getUri(OpenAiUrl openAiUrl, Object... uriVariables) {
        Map<OpenAiUrl, String> configUrls = openAiProperties.getUrls();
        String url = configUrls.get(openAiUrl);
        if (!StringUtils.hasText(url)) {
            url = openAiUrl.getSuffix();
        }
        URI requestUri = uriTemplateHandler.expand(url, uriVariables);
        return requestUri;
    }


    private HttpEntity createRequestBody(Object request) {
        String jsonString = JsonUtils.toJsonString(request);
        System.out.println(jsonString);
        return new StringEntity(jsonString, Charset.defaultCharset());
    }

    private HttpEntity createRequestBody(Map<String, String> bodies) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.setCharset(CharsetUtil.defaultCharset());
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        if (MapUtils.isNotEmpty(bodies)) {
            bodies.forEach((k, v) -> {
                if (FileTools.isExists(v)) {
                    builder.addBinaryBody(k, IOUtils.toInputStream(v, CharsetUtil.defaultCharset()));
                } else {
                    builder.addTextBody(k, v);
                }
            });
        }
        return builder.build();
    }

    @SneakyThrows
    private HttpEntity createAudioBody(Resource fileResource, CreateAudioRequest request) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.setCharset(CharsetUtil.defaultCharset());
        builder.setContentType(ContentType.MULTIPART_FORM_DATA);
        HashMap<String, String> map = Maps.newHashMap();
        builder.addBinaryBody("file", fileResource.getInputStream());
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(request.getModel()).to(model -> map.put("model", model));
        mapper.from(request.getPrompt()).to(prompt -> map.put("prompt", prompt));
        mapper.from(request.getResponseFormat()).to(format -> map.put("response_format", format));
        mapper.from(request.getTemperature()).to(obj -> map.put("temperature", obj.toString()));
        mapper.from(request.getLanguage()).to(language -> map.put("language", language));
        return builder.build();
    }
}
