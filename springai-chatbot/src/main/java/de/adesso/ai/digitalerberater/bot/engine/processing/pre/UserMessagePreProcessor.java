package de.adesso.ai.digitalerberater.bot.engine.processing.pre;

import de.adesso.ai.digitalerberater.bot.engine.exception.UserMessagePreProcessingException;
import de.adesso.ai.digitalerberater.bot.engine.model.ProcessingContext;

public interface UserMessagePreProcessor<R> {
    R process(ProcessingContext context) throws UserMessagePreProcessingException;
}
