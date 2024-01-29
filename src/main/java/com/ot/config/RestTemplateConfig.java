package com.ot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ot.interceptor.RestTemplateLoggingInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    private final ObjectMapper mainObjectMapper;

    public RestTemplateConfig(@Qualifier("mainObjectMapper") ObjectMapper mainObjectMapper) {
        this.mainObjectMapper = mainObjectMapper;
    }

    private MappingJackson2HttpMessageConverter createMappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(mainObjectMapper);
        return converter;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .setConnectTimeout(Duration.ofMillis(3000))
                .setReadTimeout(Duration.ofMillis(10000))
                .interceptors(new RestTemplateLoggingInterceptor())
                .additionalMessageConverters(createMappingJackson2HttpMessageConverter())
                .build();
    }

}
