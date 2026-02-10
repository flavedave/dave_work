package de.adesso.ai.digitalerberater.bot.infrastructure.integration.mcp;

import java.util.Map;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import io.modelcontextprotocol.common.McpTransportContext;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.mcp.customizer.McpSyncClientCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableConfigurationProperties(McpStreamableHttpClientAuthProperties.class)
@RequiredArgsConstructor
@Configuration
public class McpSyncClientAuthConfig {
    private final McpStreamableHttpClientAuthProperties authProperties;

    @Bean
    public McpSyncClientCustomizer securityContextualizedMcpSyncClient() {
        return (name, spec) -> spec.transportContextProvider(() -> McpTransportContext.create(Map.ofEntries(
                Map.entry(McpTransportContextIdentifier.CONNECTION_NAME.name(), name),
                Map.entry(McpTransportContextIdentifier.SECURITY_CONTEXT.name(), SecurityContextHolder.getContext()))));
    }

    @Bean
    public McpSyncHttpClientRequestCustomizer authenticatedMcpSyncHttpClient() {
        return (builder, method, endpoint, body, context) -> {
            if (context == McpTransportContext.EMPTY) {
                return;
            }

            var connectionName = (String) context.get(McpTransportContextIdentifier.CONNECTION_NAME.name());

            McpStreamableHttpClientAuthProperties.AuthParameters authParameters =
                    authProperties.getConnections().get(connectionName);

            // ToDo: make use of JWT audience
            if (authParameters.tokenRelayEnabled()) {
                /* String jwt = ((SecurityContext) context.get(McpTransportContextIdentifier.SECURITY_CONTEXT.name()))
                .getAuthentication()
                .getCredentials()
                .toString(); */

                // ToDo: remove JWT from authParameters and use above jwt as soon as frontend is able to pass JWTs
                builder.header("Authorization", "Bearer " + authParameters.jwt());
            }

            builder.header("X-API-Key", authParameters.apiKey());
        };
    }

    private enum McpTransportContextIdentifier {
        CONNECTION_NAME,
        SECURITY_CONTEXT
    }
}
