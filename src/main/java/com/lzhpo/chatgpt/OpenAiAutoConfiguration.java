package com.lzhpo.chatgpt;

import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.util.StrUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.luna.common.net.HttpUtils;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
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
    public DefaultOpenAiClient openAiService(
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

    @Bean(name = "httpOpenAiService")
    @ConditionalOnMissingBean
    public HttpOpenAiClient openAiService(
            WeightRandom<String> apiKeyWeightRandom,
            ObjectProvider<UriTemplateHandler> uriTemplateHandlerObjectProvider) {
        UriTemplateHandler uriTemplateHandler = uriTemplateHandlerObjectProvider.getIfAvailable(() -> {
            DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
            uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
            return uriBuilderFactory;
        });
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(openAiProperties::getReadTimeout).to(e->HttpUtils.setResponseTimeout((int) e.getSeconds()));
        mapper.from(openAiProperties::getWriteTimeout).to(e->HttpUtils.setSocketTimeOut((int) e.getSeconds()));
        mapper.from(openAiProperties::getConnectTimeout).to(e->HttpUtils.setConnectTimeout((int) e.getSeconds()));
        Optional.ofNullable(openAiProperties.getProxy()).ifPresent(e-> HttpUtils.setProxy(e.getHost(), e.getPort(), e.getUsername(), e.getPassword()));
        return new HttpOpenAiClient(openAiProperties, uriTemplateHandler, apiKeyWeightRandom);
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
