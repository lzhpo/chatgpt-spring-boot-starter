package com.lzhpo.chatgpt;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.StrUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties({OpenAiProperties.class})
public class OpenAiAutoConfiguration {

    private final OpenAiProperties openAiProperties;

    @Bean
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(List<Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(interceptors).to(x -> builder.interceptors().addAll(x));
        mapper.from(openAiProperties::getReadTimeout).to(builder::readTimeout);
        mapper.from(openAiProperties::getWriteTimeout).to(builder::writeTimeout);
        mapper.from(openAiProperties::getConnectTimeout).to(builder::connectTimeout);
        return Optional.ofNullable(openAiProperties.getProxy())
                .map(proxy -> {
                    String username = proxy.getUsername();
                    String password = proxy.getPassword();
                    mapper.from(() -> StrUtil.isAllNotBlank(username, password))
                            .whenTrue()
                            .toCall(() -> builder.proxyAuthenticator((route, response) -> response.request()
                                    .newBuilder()
                                    .header(proxy.getHeaderName(), Credentials.basic(username, password))
                                    .build()));
                    builder.proxy(new Proxy(proxy.getType(), new InetSocketAddress(proxy.getHost(), proxy.getPort())));
                    return builder.build();
                })
                .orElseGet(builder::build);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAiClient openAiService(
            OkHttpClient okHttpClient,
            WeightRandom<String> apiKeyWeightRandom,
            ObjectProvider<UriTemplateHandler> uriTemplateHandlerObjectProvider) {
        UriTemplateHandler uriTemplateHandler = uriTemplateHandlerObjectProvider.getIfAvailable(() -> {
            DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
            uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
            return uriBuilderFactory;
        });
        return new DefaultOpenAiClient(okHttpClient, openAiProperties, uriTemplateHandler, apiKeyWeightRandom);
    }

    @Bean
    @ConditionalOnMissingBean
    public WeightRandom<String> apiKeyWeightRandom(OpenAiKeyProvider openAiKeyProvider) {
        List<OpenAiKey> openAiKeys = openAiKeyProvider.get();
        Set<WeightRandom.WeightObj<String>> weightObjSet = openAiKeys.stream()
                .filter(OpenAiKey::isEnabled)
                .map(obj -> new WeightRandom.WeightObj<>(obj.getKey(), obj.getWeight()))
                .collect(Collectors.toSet());
        return new WeightRandom<>(weightObjSet);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAiKeyProvider openAiKeyProvider() {
        return openAiProperties::getKeys;
    }
}
