package de.adesso.ai.digitalerberater.bot.engine.config.dialogue;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import de.adesso.ai.digitalerberater.bot.engine.config.dialogue.model.Topic;

/**
 * Binds YAML/Properties under prefix "dialogue". Example YAML:
 *
 * dialogue:
 *   topics: ...
 */
@RefreshScope
@Validated
@Data
@Component
@ConfigurationProperties(prefix = "dialogue")
public class DialogueProperties {
    @Valid
    @NotEmpty
    private List<Topic> topics;

    public Topic getTopic(String id) {
        return topics.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Topic nicht gefunden: " + id));
    }

    public boolean hasTopic(String id) {
        return topics.stream().anyMatch(t -> t.getId().equals(id));
    }
}
