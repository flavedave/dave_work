package de.adesso.ai.digitalerberater.thymeleaf;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.BotController;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.ConversationController;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot.InitiateConversationResponse;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.ConversationDTO;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.ConversationJpa;

@Controller
@RequestMapping("/thymeleaf/conversation")
@RequiredArgsConstructor
public class ThymeleafConversationController {

    private final ConversationController conversationController;

    private final BotController botController;

    @GetMapping
    public String getChatPage(Model model) {
        model.addAttribute(
                "conversation", conversationController.getAllConversations().getBody());
        return "chat";
    }

    @PostMapping
    public String createConversation() {
        InitiateConversationResponse response = botController.initiate().getBody();
        return "redirect:/thymeleaf/conversation/" + response.conversationId();
    }

    @GetMapping("/{conversationId}")
    public String selectConversation(@PathVariable("conversationId") Long conversationId, Model model) {
        List<ConversationDTO> conversationList =
                conversationController.getAllConversations().getBody();

        ConversationJpa selectedConversation =
                conversationController.getConversationById(conversationId).getBody();

        model.addAttribute("conversation", conversationList);
        model.addAttribute("selectedConversation", selectedConversation);

        return "chat";
    }
}
