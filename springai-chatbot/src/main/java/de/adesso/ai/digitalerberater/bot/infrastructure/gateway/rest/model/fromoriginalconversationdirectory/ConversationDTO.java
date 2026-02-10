package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConversationDTO {
    Long id;
    String title;
    String currentStepId;
}
