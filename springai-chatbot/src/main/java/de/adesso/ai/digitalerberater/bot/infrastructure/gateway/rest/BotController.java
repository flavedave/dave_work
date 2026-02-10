package de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot.InitiateConversationResponse;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot.PostMessageRequest;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.bot.PostMessageResponse;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.DataEntryDto;
import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.MessageDto;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.ConversationJpa;
import de.adesso.ai.digitalerberater.bot.service.BotInteractionService;

@Tag(name = "Chatbot API", description = "API for interacting with the chatbot")
@Log4j2
@RequestMapping("/bot")
@RequiredArgsConstructor
@RestController
public class BotController {

    private final BotInteractionService botInteractionService;

    @PostMapping("/initiate")
    public ResponseEntity<InitiateConversationResponse> initiate() {
        ConversationJpa conversation = botInteractionService.initiateConversation();

        var response = InitiateConversationResponse.builder()
                .conversationId(conversation.getId())
                .messages(conversation.getMessages().stream()
                        .map(m -> new MessageDto(
                                m.getId(), m.getContent(), m.getRole().name()))
                        .toList())
                .stepId(conversation.getCurrentStepId())
                .taskData(conversation.getTaskData())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Post chat message", description = "Resume the conversation by posting a chat message")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "201", description = "Message sent and processed successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
                @ApiResponse(responseCode = "404", description = "Conversation not found")
            })
    @PostMapping("/resume")
    public ResponseEntity<PostMessageResponse> resume(@Valid @RequestBody PostMessageRequest req) {
        ConversationJpa conversation = botInteractionService.resumeConversation(req);

        var response = PostMessageResponse.builder()
                .conversationId(conversation.getId())
                .messages(conversation.getMessages().stream()
                        .map(m -> new MessageDto(
                                m.getId(), m.getContent(), m.getRole().name()))
                        .toList())
                .dataEntries(conversation.getDataEntries().stream()
                        .map(d -> new DataEntryDto(d.getId(), d.getKeyName(), d.getValue()))
                        .toList())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
