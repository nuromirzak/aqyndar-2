package org.nurma.aqyndar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate textToSpeechRestTemplate(@Value("${voice-service.connect-timeout}") final int connectTimeout,
                                                 @Value("${voice-service.read-timeout}") final int readTimeout) {
        return new RestTemplate(clientHttpRequestFactory(connectTimeout, readTimeout));
    }


    private ClientHttpRequestFactory clientHttpRequestFactory(final int connectTimeout, final int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
