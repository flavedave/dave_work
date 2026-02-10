package de.adesso.ai.digitalerberater.bot.infrastructure.persistence.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.MessageJpa;

public interface MessageRepository extends JpaRepository<MessageJpa, Long> {}
