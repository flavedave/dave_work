package de.adesso.ai.digitalerberater.bot.engine.model.result;

import java.util.List;

import de.adesso.ai.digitalerberater.bot.engine.model.ExtractedDataEntry;

public sealed interface ProcessingResult permits ProcessingResult.Success, ProcessingResult.Failure {
    String botAnswer();

    ResultDetails details();

    record Success(
            String botAnswer,
            ResultDetails details,
            String updatedTopicId,
            List<ExtractedDataEntry> dataEntryUpdates,
            String updatedStepId)
            implements ProcessingResult {}

    record Failure(String botAnswer, ResultDetails details) implements ProcessingResult {}
}
