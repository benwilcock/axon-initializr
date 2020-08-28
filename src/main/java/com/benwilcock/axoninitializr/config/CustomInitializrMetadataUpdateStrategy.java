package com.benwilcock.axoninitializr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.support.DefaultInitializrMetadataUpdateStrategy;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class CustomInitializrMetadataUpdateStrategy implements InitializrMetadataUpdateStrategy {
    private static final Log logger = LogFactory.getLog(DefaultInitializrMetadataUpdateStrategy.class);

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public CustomInitializrMetadataUpdateStrategy(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public InitializrMetadata update(final InitializrMetadata current) {
        String url = current.getConfiguration().getEnv().getSpringBootMetadataUrl();
        List<DefaultMetadataElement> bootVersions = fetchSpringBootVersions(url)
                .stream().filter(it -> !it.getId().contains("2.4")).collect(Collectors.toList());
        if (!bootVersions.isEmpty()) {
            if (bootVersions.stream().noneMatch(DefaultMetadataElement::isDefault)) {
                // No default specified
                bootVersions.get(0).setDefault(true);
            }
            current.updateSpringBootVersions(bootVersions);
        }
        return current;
    }

    /**
     * Fetch the available Spring Boot versions using the specified service url.
     *
     * @param url the url to the spring-boot project metadata
     * @return the spring boot versions metadata or {@code null} if it could not be
     * retrieved
     */
    protected List<DefaultMetadataElement> fetchSpringBootVersions(String url) {
        if (StringUtils.hasText(url)) {
            try {
                logger.info("Fetching Spring Boot metadata from " + url);
                return new CustomSpringBootMetadataReader(this.objectMapper, this.restTemplate, url).getBootVersions();
            } catch (Exception ex) {
                logger.warn("Failed to fetch Spring Boot metadata", ex);
            }
        }
        return null;
    }
}
