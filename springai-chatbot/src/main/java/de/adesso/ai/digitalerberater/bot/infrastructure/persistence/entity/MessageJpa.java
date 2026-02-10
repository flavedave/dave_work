package de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import de.adesso.ai.digitalerberater.bot.shared.enumeration.Role;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    @JsonIgnore
    private ConversationJpa conversation;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;
}
