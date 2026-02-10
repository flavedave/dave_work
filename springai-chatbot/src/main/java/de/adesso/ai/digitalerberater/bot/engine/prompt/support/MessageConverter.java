package de.adesso.ai.digitalerberater.bot.engine.prompt.support;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import de.adesso.ai.digitalerberater.bot.engine.model.ChatMessage;

@Component
public class MessageConverter {
    public Message convert(ChatMessage message) {
        return switch (message.role()) {
            case USER -> new UserMessage(message.content());
            case ASSISTANT -> new AssistantMessage(message.content());
        };
    }
}
