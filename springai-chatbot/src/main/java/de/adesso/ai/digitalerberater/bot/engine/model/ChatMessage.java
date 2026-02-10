package de.adesso.ai.digitalerberater.bot.engine.model;

import de.adesso.ai.digitalerberater.bot.shared.enumeration.Role;

public record ChatMessage(String content, Role role) {}
