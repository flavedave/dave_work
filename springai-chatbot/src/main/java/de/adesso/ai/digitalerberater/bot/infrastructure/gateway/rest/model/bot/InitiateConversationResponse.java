package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot;

import java.util.List;

import lombok.Builder;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.MessageDto;

@Builder
public record InitiateConversationResponse(
        Long conversationId, List<MessageDto> messages, String stepId, List<String> taskData) {}
