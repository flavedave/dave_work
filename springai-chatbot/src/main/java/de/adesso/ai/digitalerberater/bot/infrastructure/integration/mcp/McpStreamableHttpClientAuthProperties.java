package de.adesso.ai.digitalerberater.bot.infrastructure.integration.mcp;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import org.springframework.ai.mcp.client.common.autoconfigure.properties.McpStreamableHttpClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(McpStreamableHttpClientProperties.CONFIG_PREFIX)
public class McpStreamableHttpClientAuthProperties {
    private final Map<String, AuthParameters> connections = new HashMap<>();

    public record AuthParameters(boolean tokenRelayEnabled, String jwt, String apiKey) {}
}
