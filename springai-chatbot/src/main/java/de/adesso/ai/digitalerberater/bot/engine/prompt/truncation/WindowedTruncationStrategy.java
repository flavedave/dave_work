package de.adesso.ai.digitalerberater.bot.engine.prompt.truncation;

import java.util.List;

import de.adesso.ai.digitalerberater.bot.engine.model.ChatMessage;

public class WindowedTruncationStrategy implements ChatHistoryTruncationStrategy {
    private final int windowSize;

    public WindowedTruncationStrategy(int windowSize) {
        if (windowSize < 0) {
            throw new IllegalArgumentException("Fenstergröße darf nicht negativ sein: " + windowSize);
        }
        this.windowSize = windowSize;
    }

    @Override
    public List<ChatMessage> apply(List<ChatMessage> chatHistory) {
        return chatHistory.stream()
                .skip(Math.max(0, chatHistory.size() - this.windowSize))
                .toList();
    }
}
