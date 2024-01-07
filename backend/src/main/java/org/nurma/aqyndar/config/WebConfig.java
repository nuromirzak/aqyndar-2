package org.nurma.aqyndar.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@Log4j2
public class WebConfig {

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @Value("${voice-service.storage.type}")
    private String storageType;

    @Value("${voice-service.storage.local-path}")
    private String localPath;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(final CorsRegistry registry) {
                String[] origins = allowedOrigins.split(",");
                log.info("Allowed origins: {}", Arrays.toString(origins));
                registry.addMapping("/**")
                        .allowedOrigins(origins)
                        .allowedMethods("GET", "POST", "PATCH", "DELETE")
                        .allowedHeaders("*");
            }

            @Override
            public void addResourceHandlers(final ResourceHandlerRegistry registry) {
                WebConfig.this.addResourceHandlers(registry);
            }
        };
    }

    private void addResourceHandlers(final ResourceHandlerRegistry registry) {
        if (storageType.equals("local")) {
            log.info("Add resource handlers for local storage");
            registry.addResourceHandler("/files/**")
                    .addResourceLocations("file:" + localPath);
        }
    }
}
