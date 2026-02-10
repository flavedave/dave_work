package de.adesso.ai.digitalerberater.bot.engine.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProcessingContext {
    private final String userMessageText;

    private String botAnswer;

    private final String initialTopicId;

    private String extractedTopicId;

    private final List<ExtractedDataEntry> initialDataEntries;

    private List<ExtractedDataEntry> extractedDataEntryUpdates = new ArrayList<>();

    private final String initialStepId;

    private String extractedStepId;

    private final List<ChatMessage> chatHistory;
}
