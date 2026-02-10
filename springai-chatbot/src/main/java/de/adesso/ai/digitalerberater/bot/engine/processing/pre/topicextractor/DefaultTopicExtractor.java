package de.adesso.ai.digitalerberater.bot.engine.processing.pre.topicextractor;

import java.util.Optional;
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
import de.adesso.ai.digitalerberater.bot.engine.exception.TopicExtractionException;
import de.adesso.ai.digitalerberater.bot.engine.interceptor.structuredoutput.IdentifierOutputConverter;
import de.adesso.ai.digitalerberater.bot.engine.model.ProcessingContext;
import de.adesso.ai.digitalerberater.bot.engine.prompt.PromptBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultTopicExtractor implements TopicExtractor {
    private static final double TEMPERATURE = 0.0;

    private final DialogueProperties dialogueProperties;
    private final ObjectProvider<PromptBuilder> promptBuilderProvider;
    private final ChatClient chatClient;
    private final IdentifierOutputConverter identifierOutputConverter;

    @Autowired
    public DefaultTopicExtractor(
            DialogueProperties dialogueProperties,
            ObjectProvider<PromptBuilder> promptBuilderProvider,
            ChatClient.Builder chatClientBuilder) {
        this.dialogueProperties = dialogueProperties;
        this.promptBuilderProvider = promptBuilderProvider;
        this.chatClient = chatClientBuilder
                .defaultOptions(ChatOptions.builder().temperature(TEMPERATURE).build())
                .build();
        this.identifierOutputConverter = new IdentifierOutputConverter();
    }

    @Override
    public String process(ProcessingContext context) throws TopicExtractionException {
        Prompt prompt = promptBuilderProvider
                .getObject()
                .system(buildSystemPrompt(context.getInitialTopicId()))
                .user(context.getUserMessageText())
                .build();

        String extractedTopicId = chatClient.prompt(prompt).call().entity(identifierOutputConverter);

        log.info(extractedTopicId);

        return Optional.ofNullable(extractedTopicId)
                .filter(dialogueProperties::hasTopic)
                .orElseThrow(() -> new TopicExtractionException(
                        "Parsen des Topics ist fehlgeschlagen für den angegebenen Identifikator: " + extractedTopicId));
    }

    private String buildSystemPrompt(String currentTopicId) {
        String availableTopics = dialogueProperties.getTopics().stream()
                .map(t -> "- %s: %s".formatted(t.getId(), t.getDescription()))
                .collect(Collectors.joining("\n"));

        return """
        # ROLE
        Du bist ein Topic-Extractor für einen Versicherungs-Chatbot.

        # CONTEXT
        Bisheriges Topic: %s

        # VERFÜGBARE TOPICS
        %s

        # INSTRUCTIONS
        1. Bestimme, welches Topic aus der obigen Liste am besten zur aktuellen Nutzernachricht passt.\\n".
        3. Wenn das bisherige Topic nicht mehr passt, wechsle zu dem Topic, das am besten zur neuen Nachricht passt.
        4. Wenn das bisherige Topic nicht in der Liste vorkommt, wähle die am besten passende Alternative aus der Liste.
        5. Gib keinen zusätzlichen Text, keine Begründung und keine Formatierung aus.
        6. Antworte AUSSCHLIESSLICH mit der Topic-ID.
        """.formatted(currentTopicId, availableTopics);
    }
}
