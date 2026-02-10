package de.adesso.ai.digitalerberater.bot.engine.config.dialogue.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Topic {
    @NotBlank
    private String id;

    @NotBlank
    private String topicPrompt;

    @NotBlank
    private String description;

    @Valid
    @NotEmpty
    private List<Step> steps;

    private String client;

    public Step getStep(String id) {
        return steps.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Step nicht gefunden: " + id));
    }

    public boolean hasStep(String id) {
        return steps.stream().anyMatch(s -> s.getId().equals(id));
    }
}
