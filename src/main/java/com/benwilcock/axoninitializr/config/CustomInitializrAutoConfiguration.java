package com.benwilcock.axoninitializr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InitializrProperties.class)
@AutoConfigureAfter({JacksonAutoConfiguration.class, RestTemplateAutoConfiguration.class})
public class CustomInitializrAutoConfiguration {
    @Bean
    public InitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy(
            RestTemplateBuilder restTemplateBuilder,
            ObjectMapper objectMapper
    ) {
        return new CustomInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), objectMapper);
    }
}
