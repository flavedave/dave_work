package de.adesso.ai.digitalerberater.bot.engine.processing.pre.stepextractor;

import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.adesso.ai.digitalerberater.bot.engine.config.dialogue.DialogueProperties;
import de.adesso.ai.digitalerberater.bot.engine.config.dialogue.model.*;
import de.adesso.ai.digitalerberater.bot.engine.exception.StepExtractionException;
import de.adesso.ai.digitalerberater.bot.engine.interceptor.structuredoutput.IdentifierOutputConverter;
import de.adesso.ai.digitalerberater.bot.engine.model.ProcessingContext;
import de.adesso.ai.digitalerberater.bot.engine.prompt.PromptBuilder;
import de.adesso.ai.digitalerberater.bot.engine.prompt.truncation.ChatHistoryTruncationStrategy;
import de.adesso.ai.digitalerberater.bot.engine.prompt.truncation.WindowedTruncationStrategy;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultStepExtractor implements StepExtractor {
    private static final double TEMPERATURE = 0.1;
    private static final int CHATHISTORY_SIZE_LIMIT = 10;

    private final DialogueProperties dialogueProperties;
    private final ObjectProvider<PromptBuilder> promptBuilderProvider;
    private final ChatHistoryTruncationStrategy chatHistoryTruncationStrategy;
    private final ChatClient chatClient;
    private final IdentifierOutputConverter identifierOutputConverter;

    @Autowired
    public DefaultStepExtractor(
            DialogueProperties dialogueProperties,
            ObjectProvider<PromptBuilder> promptBuilderProvider,
            ChatClient.Builder chatClientBuilder) {
        this.dialogueProperties = dialogueProperties;
        this.promptBuilderProvider = promptBuilderProvider;
        this.chatHistoryTruncationStrategy = new WindowedTruncationStrategy(CHATHISTORY_SIZE_LIMIT);
        this.chatClient = chatClientBuilder
                .defaultOptions(ChatOptions.builder().temperature(TEMPERATURE).build())
                .build();
        this.identifierOutputConverter = new IdentifierOutputConverter();
    }

    @Override
    public String process(ProcessingContext context) throws StepExtractionException {
        Topic currentTopic = dialogueProperties.getTopic(context.getExtractedTopicId());

        Prompt prompt = promptBuilderProvider
                .getObject()
                .system(buildSystemPrompt(context.getInitialStepId(), currentTopic.getSteps()))
                .truncatedChatHistory(context.getChatHistory(), chatHistoryTruncationStrategy)
                .user(context.getUserMessageText())
                .build();

        String extractedStepId = chatClient.prompt(prompt).call().entity(identifierOutputConverter);

        log.info(extractedStepId);

        return Optional.ofNullable(extractedStepId)
                .filter(currentTopic::hasStep)
                .orElseThrow(() -> new StepExtractionException(
                        "Parsen des Steps ist fehlgeschlagen für den angegebenen Identifikator: " + extractedStepId));
    }

    private String buildSystemPrompt(String currentStepId, Collection<Step> options) {
        String availableStates = options.stream()
                .map(s -> String.format("- %s: %s", s.getId(), (s.getDescription())))
                .collect(Collectors.joining("\n"));

        return """
            # ROLE
            Du bist ein State-Manager für einen Versicherungs-Chat. Deine Aufgabe ist es, den nächsten logischen State basierend auf dem Gesprächsverlauf zu bestimmen.

            # CONTEXT
            Aktueller State: %s

            # MÖGLICHE STATES
            %s

            # INSTRUCTIONS
            1. Analysiere den bisherigen Gesprächsverlauf.
            2. Wähle den passendsten State aus der obigen Liste.
            3. Gib hierbei keinen zusätzlichen Text, keine Begründung und keine Formatierung aus.
            4. Antworte AUSSCHLIESSLICH mit dem Namen des States.
            """.formatted(currentStepId, availableStates);
    }
}
