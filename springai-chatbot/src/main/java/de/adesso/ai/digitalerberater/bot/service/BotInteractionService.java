package de.adesso.ai.digitalerberater.bot.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import de.adesso.ai.digitalerberater.bot.engine.BotEngine;
import de.adesso.ai.digitalerberater.bot.engine.model.*;
import de.adesso.ai.digitalerberater.bot.engine.model.result.ProcessingResult;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot.PostMessageRequest;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.ConversationJpa;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.DataEntryJpa;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.MessageJpa;
import de.adesso.ai.digitalerberater.bot.shared.enumeration.Role;

@Log4j2
@Transactional
@RequiredArgsConstructor
@Service
public class BotInteractionService {
    private final BotEngine botEngine;
    private final ConversationService conversationService;

    /**
     * Initialisiert eine neue Konversation mit einer Willkommensnachricht.
     * @return Die gespeicherte Konversation mit initialer Nachricht
     */
    public ConversationJpa initiateConversation() {
        var created = new ConversationJpa();

        created.setCurrentTopicId("GENERAL");
        created.setCurrentStepId("WELCOME");
        created.getMessages()
                .add(createMessage(
                        created,
                        Role.ASSISTANT,
                        "Willkommen! Gerne unterstütze ich Sie bei Ihrem Anliegen. "
                                + "Erzählen Sie mir etwas über Ihre aktuelle Situation oder stellen Sie "
                                + "mir Ihre Fragen zu unseren Versicherungen – ich helfe Ihnen gerne weiter."));

        return conversationService.save(created);
    }

    public ConversationJpa resumeConversation(PostMessageRequest request) {
        var existing = conversationService.getById(request.conversationId());

        var command = new ProcessingCommand(
                request.content(),
                existing.getCurrentTopicId(),
                existing.getDataEntries().stream()
                        .map(d -> new ExtractedDataEntry(d.getKeyName(), d.getValue(), false))
                        .toList(),
                existing.getCurrentStepId(),
                existing.getMessages().stream()
                        .map(m -> new ChatMessage(m.getContent(), m.getRole()))
                        .toList());

        var updated = applyProcessingResult(botEngine.process(command), existing);

        return conversationService.save(updated);
    }

    private ConversationJpa applyProcessingResult(ProcessingResult result, ConversationJpa conversation) {
        log.info("Applying processing result to conversation with id: {}", conversation.getId());

        conversation
                .getMessages()
                .addAll(List.of(
                        createMessage(conversation, Role.USER, result.details().processedUserMessageText()),
                        createMessage(conversation, Role.ASSISTANT, result.botAnswer())));

        if (result instanceof ProcessingResult.Success success) {
            conversation.setCurrentTopicId(success.updatedTopicId());
            conversation.setCurrentStepId(success.updatedStepId());
            syncDataEntries(conversation, success.dataEntryUpdates());
        }

        return conversation;
    }

    private void syncDataEntries(ConversationJpa conversation, List<ExtractedDataEntry> updates) {
        Map<String, DataEntryJpa> dataEntriesMap = conversation.getDataEntries().stream()
                .collect(Collectors.toMap(
                        DataEntryJpa::getKeyName, Function.identity(), (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        for (ExtractedDataEntry entry : updates) {
            String key = entry.key();

            if (entry.deleted()) {
                dataEntriesMap.remove(key);
            } else {
                DataEntryJpa entity = dataEntriesMap.computeIfAbsent(key, k -> {
                    DataEntryJpa newEntry = new DataEntryJpa();
                    newEntry.setKeyName(k);
                    newEntry.setConversation(conversation);
                    return newEntry;
                });

                entity.setValue(entry.value());
            }
        }

        conversation.getDataEntries().clear();
        conversation.getDataEntries().addAll(dataEntriesMap.values());
    }

    private MessageJpa createMessage(ConversationJpa conversation, Role role, String content) {
        var created = new MessageJpa();
        created.setConversation(conversation);
        created.setRole(role);
        created.setContent(content);
        return created;
    }
}
