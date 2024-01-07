package org.nurma.aqyndar.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestRestTemplateConfig {

    @Bean
    @Primary
    public RestTemplate restTemplateMock() {
        return Mockito.mock(RestTemplate.class);
    }
}
