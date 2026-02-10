package de.adesso.ai.digitalerberater.bot.engine.exception;

public class ResponseGenerationException extends UserMessageProcessingException {
    public ResponseGenerationException(String message) {
        super(message);
    }
}
