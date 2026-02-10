package de.adesso.ai.digitalerberater.thymeleaf;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.BotController;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot.PostMessageRequest;

@Controller
@RequestMapping("/thymeleaf/chat")
@RequiredArgsConstructor
public class ThymeleafMessageController {

    private final BotController botController;

    @PostMapping
    public String postChatMessage(
            @RequestParam("conversationId") Long conversationId, @RequestParam("messageText") String messageText) {
        var req = new PostMessageRequest(conversationId, messageText);

        botController.resume(req);

        return "redirect:/thymeleaf/conversation/" + conversationId;
    }
}
