![](https://img.shields.io/badge/JDK-1.8+-success.svg)
![](https://maven-badges.herokuapp.com/maven-central/com.lzhpo/chatgpt-spring-boot-starter/badge.svg?color=blueviolet)
![](https://img.shields.io/:license-Apache2-orange.svg)
[![Style check](https://github.com/lzhpo/chatgpt-spring-boot-starter/actions/workflows/style-check.yml/badge.svg)](https://github.com/lzhpo/chatgpt-spring-boot-starter/actions/workflows/style-check.yml)

## 概览

1. 支持设置多个 API Key，并且支持对其设置权重以及是否启用，支持自动禁用失效的 API Key 以及自动轮转
2. 支持设置请求代理
3. 支持自定义请求 API（如果对 OpenAi 的 API 做了中转/代理）
4. 支持OpenAi所有可以使用 API Key 访问的 API
5. 支持流式响应，即所谓的"打字机"模式
6. 请求参数自动校验
7. 支持token计算

## 支持的功能

✅ 模型查询（Model）<br>
✅ 流式、非流式对话聊天（Stream Chat/completion）<br>
✅ 根据提示生成文本（Edit）<br>
✅ 自然语言转换为向量表示<br>
✅ 音频、视频语音转文本（Create transcription）<br>
✅ 文本翻译（Create translation）<br>
✅ 文件的查询、上传、删除（File - List/Upload/Delete/Retrieve）<br>
✅ 预训练模型的微调、查询、放弃、过程(事件)（Fine-tunes - Create/List/Retrieve/Cancel/Events）<br>
✅ 内容审核（Moderation）<br>
✅ 用户余额、使用量查询（Billing/Usage）<br>
✅ 用户信息查询（User）<br>
✅ 根据提示创建、编辑图像、根据图像生成多版本图像（Image - Create/Create edit/Create variation）

## 项目地址

- Github: https://github.com/lzhpo/chatgpt-spring-boot-starter
- Gitee: https://gitee.com/lzhpo/chatgpt-spring-boot-starter

## 导入依赖

```xml
<dependency>
    <groupId>com.lzhpo</groupId>
    <artifactId>chatgpt-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>
```

## 配置示例

### 1. 支持配置多个 API Key（权重、是否启用）

> 可以对当前 api key 设置权重，以及是否需要启用此 api key，提供了两种方式配置。

#### 1.1 方式1-常规yaml配置方式

```yaml
openai:
  keys:
    - key: "sk-xxx1"
      weight: 1.0
      enabled: true
    - key: "sk-xxx2"
      weight: 2.0
      enabled: false
    - key: "sk-xxx3"
      weight: 3.0
      enabled: false
```

_支持自动禁用失效的 API Key 以及自动轮转，参考：`InvalidedKeyEvent`、`NoAvailableKeyEvent`、`OpenAiEventListener`_

#### 1.2 方式2-自定义获取 API Key 逻辑

如果你的 API Key 是存在数据库或者其它地方的，那么可以选择使用这种方式配置。

实现`OpenAiKeyProvider`接口即可，例如：
```java
@Component
public class XxxOpenAiKeyProvider implements OpenAiKeyProvider {

    @Override
    public List<OpenAiKey> get() {
        List<OpenAiKey> openAiKeys = new ArrayList<>();
        openAiKeys.add(OpenAiKey.builder().key("sk-xxx1").weight(1.0).enabled(true).build());
        openAiKeys.add(OpenAiKey.builder().key("sk-xxx2").weight(2.0).enabled(false).build());
        openAiKeys.add(OpenAiKey.builder().key("sk-xxx2").weight(3.0).enabled(true).build());
        return openAiKeys;
    }
}
```

**注意：每次请求都会调用此方法，有需要的话可以在此加一个缓存。**

### 2. 支持配置代理

```yaml
openai:
  proxy:
    host: "127.0.0.1"
    port: 7890
    type: http
    header-name: "Proxy-Authorization"
    username: admin
    password: 123456
```

### 3. 支持配置超时时间

```yaml
openai:
  connect-timeout: 1m
  read-timeout: 1m
  write-timeout: 1m
```

### 4. 支持自定义请求API

> 如果没有配置代理，也没有定制完整请求地址的需求，那么无需配置`openai.domain`以及`openai.urls`，会自动使用默认的。

#### 4.1 方式1 - 只配置了代理

如果只是配置了国内中转代理，那么只需要配置`openai.domain`为代理地址即可，默认值为https://api.openai.com

```yaml
openai:
  domain: "https://api.openai.com"
```

#### 4.2 方式2 - 定制完整的请求地址

如果有定制完整请求地址的需求，可以按照如下配置，优先级比`openai.domain`更高，但需要的是**完整的请求地址**。

```yaml
openai:
  urls:
    moderations: "https://api.openai.com/v1/moderations"
    completions: "https://api.openai.com/v1/completions"
    edits: "https://api.openai.com/v1/edits"
    chat-completions: "https://api.openai.com/v1/chat/completions"
    list-models: "https://api.openai.com/v1/models"
    retrieve-model: "https://api.openai.com/v1/models/{model}"
    embeddings: "https://api.openai.com/v1/embeddings"
    list-files: "https://api.openai.com/v1/files"
    upload-file: "https://api.openai.com/v1/files"
    delete-file: "https://api.openai.com/v1/files/{file_id}"
    retrieve-file: "https://api.openai.com/v1/files/{file_id}"
    retrieve-file-content: "https://api.openai.com/v1/files/{file_id}/content"
    create_fine_tune: "https://api.openai.com/v1/fine-tunes"
    list_fine_tune: "https://api.openai.com/v1/fine-tunes"
    retrieve_fine_tune: "https://api.openai.com/v1/fine-tunes/{fine_tune_id}"
    cancel_fine_tune: "https://api.openai.com/v1/fine-tunes/{fine_tune_id}/cancel"
    list_fine_tune_events: "https://api.openai.com/v1/fine-tunes/{fine_tune_id}/events"
    delete_fine_tune_events: "https://api.openai.com/v1/models/{model}"
    create-transcription: "https://api.openai.com/v1/audio/transcriptions"
    create-translation: "https://api.openai.com/v1/audio/translations"
    create_image: "https://api.openai.com/v1/images/generations"
    create_image_edit: "https://api.openai.com/v1/images/edits"
    create_image_variation: "https://api.openai.com/v1/images/variations"
    billing-credit-grants: "https://api.openai.com/dashboard/billing/credit_grants"
    users: "https://api.openai.com/v1/organizations/{organizationId}/users"
    billing-subscription: "https://api.openai.com/v1/dashboard/billing/subscription"
    billing-usage: "https://api.openai.com/v1/dashboard/billing/usage?start_date={start_date}&end_date={end_date}"
```

### 5. 支持token计算

示例1：
```java
Long tokens = TokenUtils.tokens(model, content);
```

示例2：`CompletionRequest`
```java
CompletionRequest request = new CompletionRequest();
// request.setXXX 略...
Long tokens = TokenUtils.tokens(request.getModel(), request.getPrompt());
```

示例3：`ChatCompletionRequest`
```java
ChatCompletionRequest request = new ChatCompletionRequest();
// request.setXXX 略...
Long tokens = TokenUtils.tokens(request.getModel(), request.getMessages());
```

OpenAi返回的token计算结果可在response返回体中获取：
- `prompt_tokens`：OpenAi计算的输入消耗的token
- `completion_tokens`：OpenAi计算的输出消耗的token
- `total_tokens`：`prompt_tokens` + `completion_tokens`

具体可参考测试用例`OpenAiCountTokensTest`以及`TokenUtils`

### 6. 关于异常处理

1. 常规、SSE以及WebSocket请求失败均会抛出`OpenAiException`异常，可自定义全局异常，取出OpenAi的响应结果转换为`OpenAiError`(如果转换结果`OpenAiError`不为空)，继而自行处理。
2. 自定义流式处理的`EventSourceListener`，推荐继承`AbstractEventSourceListener`，如果没有特殊需求，直接重写`onEvent`方法即可，如果重写了`onFailure`方法，抛出何种异常取决于重写的`onFailure`方法。
3. 提供了失效的Api-Key事件、当前无可用的Api-Key事件，可自行监听处理，例如：
    ```java
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
    ```
4. 也可以实现OKHttp的`Interceptor`接口，并声明为Bean，也可在里面自行处理异常，参考：`OpenAiErrorInterceptor`

## 代码示例

### 1. 流式输出（“打字机”）

#### 1.1 SSE方式示例

**这里快速了解一下SSE（Server-Sent Events）**

SSE和WebSocket都是用于实现服务器和浏览器之间实时通信的技术。
WebSocket是全双工通信协议，适用于双向通信的实时场景，而SSE是单向通信协议，适用于服务器向客户端推送消息的实时场景。

简单网页效果示例（“打字机”效果）：
![](./does/images/SSE-Stream-Chat.png)

后端代码简单示例：
```java
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class ChatController {

    private final OpenAiClient openAiClient;

    @GetMapping("/chat/sse")
    public SseEmitter sseStreamChat(@RequestParam String message) {
        SseEmitter sseEmitter = new SseEmitter();
        ChatCompletionRequest request = ChatCompletionRequest.create(message);
        openAiClient.streamChatCompletions(request, new SseEventSourceListener(sseEmitter));
        return sseEmitter;
    }
}
```
`SseEventSourceListener`是基于`okhttp3.sse.EventSourceListener`实现的，可以接收`text/event-stream`类型的流式数据。

前端代码简单示例：
```javascript
// message为需要发送的消息
const eventSource = new EventSource(`http://127.0.0.1:6060/chat/sse?message=${message}`);
// 收到消息处理
eventSource.onmessage = function(event) {
    // 略...
}
```

由于SSE协议只支持GET方法，不支持POST方法。<br/>
如果要支持POST方法可以参考：https://github.com/Azure/fetch-event-source

详细代码见仓库目录下：
- `templates/chat.html`
- `templates/sse-stream-chat.html`
- `com.lzhpo.chatgpt.OpenAiTestController`

#### 1.2 WebSocket方式示例

效果和SSE方式一样，即“打字机”效果。

声明WebSocket端点：
```java
@Slf4j
@Component
@ServerEndpoint("/chat/websocket")
public class OpenAiWebSocketTest {

    @OnOpen
    public void onOpen(Session session) {
        log.info("sessionId={} joined.", session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("Received sessionId={} message={}", session.getId(), message);
        ChatCompletionRequest request = ChatCompletionRequest.create(message);
        WebSocketEventSourceListener listener = new WebSocketEventSourceListener(session);
        SpringUtil.getBean(OpenAiClient.class).streamChatCompletions(request, listener);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("Closed sessionId={} connection.", session.getId());
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.error("sessionId={} error: {}", session.getId(), e.getMessage(), e);
    }
}
```
开启WebSocket端点：
```java
@Bean
public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
}
```

前端代码主要逻辑如下：
```javascript
const websocket = new WebSocket("ws://127.0.0.1:6060/chat/websocket");
// 发送消息（message为需要发送的消息）
websocket.send(message);
// 收到消息
websocket.onmessage = function(event) {
    // 略...
}
```
详细代码见仓库目录下的`templates/websocket-stream-chat.html`

### 2. 自定义请求拦截器

实现`okhttp3.Interceptor`接口，并将其声明为bean即可。

例如：
```java
@Slf4j
public class OpenAiLoggingInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        log.info("Request url: {} {}", request.method(), request.url());
        log.info("Request header: {}", request.headers());

        Response response = chain.proceed(request);
        log.info("Response code: {}", response.code());
        log.info("Response body: {}", response.body().string());
        return response;
    }
}
```

### 3. 全部API测试示例

见：`com.lzhpo.chatgpt.OpenAiClientTest`

```java
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OpenAiClientTest {

    @Autowired
    private OpenAiClient openAiService;

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
        CountDownLatchEventSourceListener eventSourceListener = new CountDownLatchEventSourceListener(countDownLatch);
        assertDoesNotThrow(() -> openAiService.streamCompletions(request, eventSourceListener));
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

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CountDownLatchEventSourceListener eventSourceListener = new CountDownLatchEventSourceListener(countDownLatch);
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
```

## 微信公众号

<img src="./does/images/WeChat-MP.png" width="453" height="150" alt="会打篮球的程序猿">
