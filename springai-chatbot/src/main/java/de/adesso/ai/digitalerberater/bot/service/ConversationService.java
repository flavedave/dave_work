package de.adesso.ai.digitalerberater.bot.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import de.adesso.ai.digitalerberater.bot.infrastructure.gateway.rest.model.fromoriginalconversationdirectory.ConversationDTO;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.ConversationJpa;
import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.repo.ConversationRepository;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository conversationRepository;

    public List<ConversationDTO> findTitleByUserIdOrderByUpdatedAtDesc(String userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(conversation -> new ConversationDTO(
                        conversation.getId(), conversation.getTitle(), conversation.getCurrentStepId()))
                .collect(Collectors.toList());
    }

    public ConversationJpa getById(Long conversationId) {
        return conversationRepository
                .findById(conversationId)
                .orElseThrow(() -> new NoSuchElementException("Conversation not found with id: " + conversationId));
    }

    public ConversationJpa save(ConversationJpa conversation) {
        return conversationRepository.save(conversation);
    }
}
