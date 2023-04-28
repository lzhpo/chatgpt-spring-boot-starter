package com.lzhpo.chatgpt;

import cn.hutool.core.util.StrUtil;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.luna.common.net.HttpUtils;
import com.luna.common.net.high.AsyncHttpUtils;
import lombok.RequiredArgsConstructor;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
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
            OpenAiKeyWrapper openAiKeyWrapper,
            ObjectProvider<UriTemplateHandler> uriTemplateHandlerObjectProvider) {
        UriTemplateHandler uriTemplateHandler = uriTemplateHandlerObjectProvider.getIfAvailable(() -> {
            DefaultUriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();
            uriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.URI_COMPONENT);
            return uriBuilderFactory;
        });
        return new DefaultOpenAiClient(okHttpClient, openAiProperties, uriTemplateHandler, openAiKeyWrapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAiKeyWrapper openAiKeyWrapper(OpenAiKeyProvider openAiKeyProvider) {
        return new OpenAiKeyWrapper(openAiKeyProvider);
    }

    @Bean(name = "httpOpenAiService")
    @ConditionalOnMissingBean
    public HttpOpenAiClient openAiService(
            OpenAiKeyWrapper openAiKeyWrapper,
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
        Optional.ofNullable(openAiProperties.getProxy()).ifPresent(e-> AsyncHttpUtils.setProxy(e.getHost(), e.getPort()));
        return new HttpOpenAiClient(openAiProperties, uriTemplateHandler, openAiKeyWrapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAiKeyProvider openAiKeyProvider() {
        return openAiProperties::getKeys;
    }
}
