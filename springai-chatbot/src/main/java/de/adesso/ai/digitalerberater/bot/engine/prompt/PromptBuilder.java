package de.adesso.ai.digitalerberater.bot.engine.prompt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import de.adesso.ai.digitalerberater.bot.engine.model.ChatMessage;
import de.adesso.ai.digitalerberater.bot.engine.prompt.support.MessageConverter;
import de.adesso.ai.digitalerberater.bot.engine.prompt.truncation.ChatHistoryTruncationStrategy;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class PromptBuilder {
    private final MessageConverter messageConverter;

    private final List<Message> messages;

    @Nullable
    private ChatOptions chatOptions;

    @Autowired
    public PromptBuilder(MessageConverter messageConverter) {
        this.messageConverter = messageConverter;
        this.messages = new ArrayList<>();
    }

    public PromptBuilder system(String text) {
        messages.add(new SystemMessage(text));
        return this;
    }

    public PromptBuilder truncatedChatHistory(
            List<ChatMessage> chatHistory, ChatHistoryTruncationStrategy truncationStrategy) {
        List<Message> truncatedChatHistory = truncationStrategy.apply(chatHistory).stream()
                .map(messageConverter::convert)
                .toList();
        messages.addAll(truncatedChatHistory);
        return this;
    }

    public PromptBuilder user(String text) {
        messages.add(new UserMessage(text));
        return this;
    }

    public PromptBuilder chatOptions(ChatOptions chatOptions) {
        this.chatOptions = chatOptions;
        return this;
    }

    public Prompt build() {
        return new Prompt(this.messages, this.chatOptions);
    }
}
