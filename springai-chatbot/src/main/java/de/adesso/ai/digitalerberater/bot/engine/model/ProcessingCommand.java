package de.adesso.ai.digitalerberater.bot.engine.model;

import java.util.List;

public record ProcessingCommand(
        String userMessageText,
        String currentTopicId,
        List<ExtractedDataEntry> currentDataEntries,
        String currentStepId,
        List<ChatMessage> chatHistory) {}
