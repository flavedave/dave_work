package de.adesso.ai.digitalerberater.bot.infrastructure.persistence.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.ConversationJpa;

public interface ConversationRepository extends JpaRepository<ConversationJpa, Long> {

    Optional<ConversationJpa> findByUserIdAndId(String userId, Long id);

    List<ConversationJpa> findByUserIdOrderByUpdatedAtDesc(String userId);
}
