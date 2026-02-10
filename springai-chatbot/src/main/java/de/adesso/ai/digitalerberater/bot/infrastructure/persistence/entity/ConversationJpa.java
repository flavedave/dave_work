package de.adesso.ai.digitalerberater.bot.infrastructure.persistence.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

// ToDo: move jackson annotations and wrongly placed fields to corresponding Rest-Response
@Entity
@Table(name = "conversations")
@Getter
@Setter
public class ConversationJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title = "Neue Konversation";

    private String currentStepId;

    private String currentTopicId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String userId;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MessageJpa> messages = new ArrayList<>();

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DataEntryJpa> dataEntries = new ArrayList<>();

    private List<String> taskData =
            List.of("Frage zu einem Vertrag", "Beratung zu einer Versicherung", "Versicherungsanalyse");

    private String template;

    private String scope;
}
