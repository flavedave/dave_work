package de.adesso.ai.digitalerberater.bot.engine.prompt.truncation;

import java.util.List;

import de.adesso.ai.digitalerberater.bot.engine.model.ChatMessage;

public interface ChatHistoryTruncationStrategy {
    List<ChatMessage> apply(List<ChatMessage> chatHistory);
}
