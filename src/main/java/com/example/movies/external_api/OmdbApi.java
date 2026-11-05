package com.example.movies.external_api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OmdbApi {

    @Value("${external.omdb.base-url}")
    private String baseUrl;

    @Value("${external.omdb.connect-timeout-ms}")
    private int connectTimeout;

    @Value("${external.omdb.read-timeout-ms}")
    private int readTimeout;


    @Bean
    public RestClient ombdClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }


}
