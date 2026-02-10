package de.adesso.ai.digitalerberater.bot.engine.config.dialogue.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Step {
    @NotBlank
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    // ToDo: overthink step prompt concept
    private String prompt;
}
