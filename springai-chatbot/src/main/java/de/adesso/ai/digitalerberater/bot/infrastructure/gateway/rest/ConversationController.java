package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.ConversationDTO;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.ConversationJpa;
import de.adesso.ai.digitalerberater.bot.service.ConversationService;
import de.adesso.ai.digitalerberater.bot.service.looks_like_util_and_or_config.MarkdownService;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
@Tag(name = "Conversation API", description = "API for managing chat conversations")
public class ConversationController {

    private final ConversationService conversationService;

    private final MarkdownService markdownService;

    @Operation(
            summary = "Get all conversations",
            description = "Retrieve all conversations for the sidebar, ordered by updated date")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully retrieved conversations")})
    @GetMapping
    public ResponseEntity<List<ConversationDTO>> getAllConversations() {
        return ResponseEntity.ok(conversationService.findTitleByUserIdOrderByUpdatedAtDesc("1"));
    }

    @Operation(summary = "Select conversation", description = "Retrieve a specific conversation by ID for display")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Conversation found successfully"),
                @ApiResponse(responseCode = "404", description = "Conversation not found")
            })
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationJpa> getConversationById(
            @Parameter(description = "ID of the conversation to retrieve") @PathVariable("conversationId")
                    Long conversationId) {

        // ToDo: use ConversationDto
        var conv = conversationService.getById(conversationId);
        conv.getMessages().forEach(m -> m.setContent(markdownService.toHtml(m.getContent())));
        return ResponseEntity.ok(conv);
    }
}
