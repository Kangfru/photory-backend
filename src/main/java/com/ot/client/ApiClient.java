package com.ot.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.ot.client.model.CommonRes;
import com.ot.client.model.ContainResultMixin;
import com.ot.client.model.search.ResultInterface;
import com.ot.client.model.search.ResultMixin;
import com.ot.client.model.search.provider.ContentsProviderSearchRes;
import com.ot.config.AppConfig;
import com.ot.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiClient {

    private final AppConfig appConfig;

    private final RestTemplate restTemplate;

    private final ObjectMapper mainObjectMapper;

    public <S, R> R get(String url, HttpHeaders httpHeaders, S requestBody, Class<R> responseClass) {
        return callApiEndPoint(url, HttpMethod.GET, httpHeaders, requestBody, responseClass);
    }

    public <S, R> R get(String url, HttpHeaders httpHeaders, Class<R> responseClass) {
        return callApiEndPoint(url, HttpMethod.GET, httpHeaders, null, responseClass);
    }

    public <S, R> R get(String url, HttpHeaders httpHeaders, Class<R> responseClass, MultiValueMap<String, String> uriVariables) {
        UriComponents uri = UriComponentsBuilder.fromUriString(url)
                .queryParams(uriVariables)
                .build();
        return callApiEndPoint(uri.toUriString(), HttpMethod.GET, httpHeaders, null, responseClass);
    }

    public <S, R> R get(String url, HttpHeaders httpHeaders, Class<R> responseClass, MultiValueMap<String, String> queryParams, Map<String, Object> uriParams) {
        UriComponents uri = UriComponentsBuilder.fromUriString(url)
                .queryParams(queryParams)
                .uriVariables(uriParams)
                .build();
        return callApiEndPoint(uri.toUriString(), HttpMethod.GET, httpHeaders, null, responseClass);
    }

    public <S,R> R post(String url, HttpHeaders httpHeaders, S requestBody, Class<R> responseClass) {
        return callApiEndPoint(url, HttpMethod.POST, httpHeaders, requestBody, responseClass);
    }

    private <S, R> R callApiEndPoint(String url, HttpMethod httpMethod, HttpHeaders httpHeaders, S requestBody, Class<R> responseClass) {
        R res = null;
        HttpEntity<S> requestHttpEntity = new HttpEntity<>(requestBody, httpHeaders);
        try {
            ResponseEntity<String> responseEntity = null;
            responseEntity = restTemplate.exchange(url, httpMethod, requestHttpEntity, String.class);
            if (ContainResultMixin.class.isAssignableFrom(responseClass)) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                objectMapper.addMixIn(ResultInterface.class, ResultMixin.class);
                res = objectMapper.readValue(responseEntity.getBody(), responseClass);
                return res;
            }
            if (ContentsProviderSearchRes.class.isAssignableFrom(responseClass)) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Map<String, Object> readMap = objectMapper.readValue(responseEntity.getBody(), new TypeReference<>() {});
                LinkedHashMap<String, Object> contentsMap = (LinkedHashMap<String, Object>) readMap.get("results");
                if (!contentsMap.containsKey("KR")) {
                    throw new ServiceException("500", "PARAMETER를 확인하세요.", HttpStatus.BAD_REQUEST);
                }
                return objectMapper.convertValue(contentsMap.get("KR"), responseClass);
            }
            res = mainObjectMapper.readValue(responseEntity.getBody(), responseClass);
        } catch (JsonMappingException e) {
            log.error("==== JSON Parsing Exception ::: ", e);
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            log.error("==== JSON Parsing Exception ::: ", e);
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            try {
                CommonRes commonRes = mainObjectMapper.readValue(e.getResponseBodyAsString(), CommonRes.class);
                if (!commonRes.isSuccess()) {
                    throw new ServiceException(String.valueOf(e.getStatusCode().value()), commonRes.getStatusMessage(), e.getStatusCode());
                }
            } catch (JsonMappingException ex) {
                throw new RuntimeException(ex);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }
        return res;
    }
}
