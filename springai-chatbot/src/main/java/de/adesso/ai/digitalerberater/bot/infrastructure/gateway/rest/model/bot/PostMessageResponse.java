package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot;

import java.util.List;

import lombok.Builder;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.DataEntryDto;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.MessageDto;

@Builder
public record PostMessageResponse(Long conversationId, List<MessageDto> messages, List<DataEntryDto> dataEntries) {}
