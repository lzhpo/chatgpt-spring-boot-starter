package com.lzhpo.chatgpt;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.StrUtil;
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
    public OkHttpClient okHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return Optional.ofNullable(openAiProperties.getProxy())
                .map(proxy -> {
                    String username = proxy.getUsername();
                    String password = proxy.getPassword();
                    PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
                    mapper.from(proxy::getReadTimeout).to(builder::readTimeout);
                    mapper.from(proxy::getWriteTimeout).to(builder::writeTimeout);
                    mapper.from(proxy::getConnectTimeout).to(builder::connectTimeout);
                    mapper.from(() -> StrUtil.isAllNotBlank(username, password))
                            .whenTrue()
                            .toCall(() -> builder.proxyAuthenticator((route, response) -> response.request()
                                    .newBuilder()
                                    .header(OpenAiConstant.PROXY_AUTHORIZATION, Credentials.basic(username, password))
                                    .build()));
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
    public WeightRandom<String> apiKeyWeightRandom(OpenAiProperties openAiProperties) {
        Set<WeightRandom.WeightObj<String>> weightObjSet = openAiProperties.getKeys().stream()
                .filter(OpenAiKeyWeight::isEnabled)
                .map(obj -> new WeightRandom.WeightObj<>(obj.getKey(), obj.getWeight()))
                .collect(Collectors.toSet());
        return new WeightRandom<>(weightObjSet);
    }
}
