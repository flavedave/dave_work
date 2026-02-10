package de.adesso.ai.digitalerberater.bot.engine.processing.post;

import de.adesso.ai.digitalerberater.bot.engine.exception.ResponseGenerationException;
import de.adesso.ai.digitalerberater.bot.engine.model.ProcessingContext;

public interface ResponseGenerator {
    String respond(ProcessingContext context) throws ResponseGenerationException;

    String recover(ProcessingContext context);
}
