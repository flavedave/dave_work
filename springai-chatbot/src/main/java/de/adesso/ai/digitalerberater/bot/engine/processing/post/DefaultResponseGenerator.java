package de.adesso.ai.digitalerberater.bot.engine.processing.post;

import static java.util.function.Predicate.not;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.adesso.ai.digitalerberater.bot.engine.config.dialogue.DialogueProperties;
import de.adesso.ai.digitalerberater.bot.engine.config.dialogue.model.Topic;
import de.adesso.ai.digitalerberater.bot.engine.exception.ResponseGenerationException;
import de.adesso.ai.digitalerberater.bot.engine.model.ProcessingContext;
import de.adesso.ai.digitalerberater.bot.engine.prompt.PromptBuilder;
import de.adesso.ai.digitalerberater.bot.engine.prompt.truncation.ChatHistoryTruncationStrategy;
import de.adesso.ai.digitalerberater.bot.engine.prompt.truncation.WindowedTruncationStrategy;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultResponseGenerator implements ResponseGenerator {
    private static final double TEMPERATURE = 0.1;
    private static final int CHATHISTORY_SIZE_LIMIT = 10;

    private final DialogueProperties dialogueProperties;
    private final ObjectProvider<PromptBuilder> promptBuilderProvider;
    private final ChatHistoryTruncationStrategy chatHistoryTruncationStrategy;
    private final ChatClient chatClient;

    // ToDo: Vector Store

    @Autowired
    public DefaultResponseGenerator(
            DialogueProperties dialogueProperties,
            ObjectProvider<PromptBuilder> promptBuilderProvider,
            ObjectProvider<ToolCallbackProvider> toolCallbackProvider,
            ChatClient.Builder chatClientBuilder) {
        this.dialogueProperties = dialogueProperties;
        this.promptBuilderProvider = promptBuilderProvider;
        this.chatHistoryTruncationStrategy = new WindowedTruncationStrategy(CHATHISTORY_SIZE_LIMIT);
        this.chatClient = chatClientBuilder
                .defaultOptions(ChatOptions.builder().temperature(TEMPERATURE).build())
                .defaultToolCallbacks(Optional.ofNullable(toolCallbackProvider.getIfAvailable())
                        .orElse(ToolCallbackProvider.from(List.of())))
                .build();
    }

    @Override
    public String respond(ProcessingContext context) throws ResponseGenerationException {
        Prompt prompt = promptBuilderProvider
                .getObject()
                // ToDo: integrate data entries
                .system(buildSystemPrompt(context.getExtractedTopicId(), context.getExtractedStepId()))
                .truncatedChatHistory(context.getChatHistory(), chatHistoryTruncationStrategy)
                .user(context.getUserMessageText())
                .build();

        String response = chatClient.prompt(prompt).call().content();

        return Optional.ofNullable(response)
                .filter(not(String::isBlank))
                .map(String::trim)
                .orElseThrow(() -> new ResponseGenerationException("Generierte Antwort ist ohne Inhalt"));
    }

    @Override
    public String recover(ProcessingContext context) {
        return "";
    }

    private String buildSystemPrompt(String currentTopicId, String currentStepId) {
        Topic topic = dialogueProperties.getTopic(currentTopicId);
        String topicPrompt = topic.getTopicPrompt();
        String stepPrompt = topic.getStep(currentStepId).getPrompt();

        return """
        # ROLE
        %s

        # CONTEXT
        Aktueller Step im Prozess: %s
        Ziel dieses Schritts: %s

        # INSTRUCTIONS
        1. Berücksichtige den bisherigen Gesprächsverlauf.
        2. Formuliere eine Antwort, die den Nutzer im Sinne des aktuellen Steps unterstützt.

        """.formatted(topicPrompt, currentStepId, stepPrompt);
    }
}
