package de.adesso.ai.digitalerberater.bot.infrastructure.persistence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity.DataEntryJpa;

@Repository
public interface DataEntryRepository extends JpaRepository<DataEntryJpa, Long> {}
