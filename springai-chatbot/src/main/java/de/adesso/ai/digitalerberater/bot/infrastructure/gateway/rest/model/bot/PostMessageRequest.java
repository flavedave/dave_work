package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PostMessageRequest(
        @NotNull @Positive Long conversationId, @NotBlank String content) {}
