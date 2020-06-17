package com.benwilcock.axoninitializr.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import io.spring.initializr.metadata.DefaultMetadataElement;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomSpringBootMetadataReader {
    private final JsonNode content;

    CustomSpringBootMetadataReader(ObjectMapper objectMapper, RestTemplate restTemplate, String url) throws IOException {
        this.content = objectMapper.readTree(restTemplate.getForObject(url, String.class));
    }

    /**
     * Return the boot versions parsed by this instance.
     * @return the versions
     */
    List<DefaultMetadataElement> getBootVersions() {
        ArrayNode releases = (ArrayNode) this.content.get("projectReleases");
        List<DefaultMetadataElement> list = new ArrayList<>();
        for (JsonNode node : releases) {
            DefaultMetadataElement version = new DefaultMetadataElement();
            version.setId(node.get("version").textValue());
            String name = node.get("versionDisplayName").textValue();
            version.setName(node.get("snapshot").booleanValue() ? name + " (SNAPSHOT)" : name);
            version.setDefault(node.get("current").booleanValue());
            list.add(version);
        }
        return list;
    }
}
