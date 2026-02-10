package de.adesso.ai.digitalerberater.bot.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import de.adesso.ai.digitalerberater.bot.engine.exception.UserMessageProcessingException;
import de.adesso.ai.digitalerberater.bot.engine.model.*;
import de.adesso.ai.digitalerberater.bot.engine.model.result.ProcessingResult;
import de.adesso.ai.digitalerberater.bot.engine.model.result.ResultDetails;
import de.adesso.ai.digitalerberater.bot.engine.processing.post.ResponseGenerator;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.dataextractor.DataExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.stepextractor.StepExtractor;
import de.adesso.ai.digitalerberater.bot.engine.processing.pre.topicextractor.TopicExtractor;

@Slf4j
@RequiredArgsConstructor
@Component
public class BotEngine {
    private final TopicExtractor topicExtractor;

    private final StepExtractor stepExtractor;

    private final DataExtractor dataExtractor;

    private final ResponseGenerator responseGenerator;

    public ProcessingResult process(ProcessingCommand command) {
        var context = new ProcessingContext(
                command.userMessageText(),
                command.currentTopicId(),
                command.currentDataEntries(),
                command.currentStepId(),
                command.chatHistory());

        try {
            context.setExtractedTopicId(topicExtractor.process(context));
            context.setExtractedStepId(stepExtractor.process(context));
            context.setExtractedDataEntryUpdates(dataExtractor.process(context));
            context.setBotAnswer(responseGenerator.respond(context));

        } catch (UserMessageProcessingException e) {
            log.error(e.getMessage());
            context.setBotAnswer(responseGenerator.recover(context));
            return new ProcessingResult.Failure(
                    context.getBotAnswer(), new ResultDetails(context.getUserMessageText()));
        }

        return new ProcessingResult.Success(
                context.getBotAnswer(),
                new ResultDetails(context.getUserMessageText()),
                context.getExtractedTopicId(),
                context.getExtractedDataEntryUpdates(),
                context.getExtractedStepId());
    }
}
