package de.adesso.ai.digitalerberater.bot.engine.processing.pre.dataextractor;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adesso.ai.digitalerberater.bot.engine.config.dialogue.DialogueProperties;
import de.adesso.ai.digitalerberater.bot.engine.exception.DataExtractionException;
import de.adesso.ai.digitalerberater.bot.engine.model.ExtractedDataEntry;
import de.adesso.ai.digitalerberater.bot.engine.model.ProcessingContext;

@Log4j2
@RequiredArgsConstructor
@Component
public class DefaultDataExtractor implements DataExtractor {
    private final DialogueProperties dialogueProperties;
    private final ObjectMapper objectMapper;
    private final ChatClient chatClient;

    @Autowired
    public DefaultDataExtractor(
            DialogueProperties dialogueProperties, ObjectMapper objectMapper, ChatClient.Builder chatClientBuilder) {
        this.dialogueProperties = dialogueProperties;
        this.objectMapper = objectMapper;
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public List<ExtractedDataEntry> process(ProcessingContext context) throws DataExtractionException {
        String prompt = """
        %s

        Versicherungsspezifische Extraktionsanweisungen:
        %s

        Bereits bekannte Daten:
        %s

        Neue Kundennachricht:
        "%s"
        """.formatted(
                        buildGenericExtractionPrompt(),
                        buildSpecificExtractionPrompt(),
                        buildKnownDataJson(context.getInitialDataEntries()),
                        context.getUserMessageText());

        try {
            String response = chatClient.prompt().user(prompt).call().content();

            log.info(response);

            return objectMapper.readValue(response, new TypeReference<>() {});

        } catch (Exception e) {
            throw new DataExtractionException(e.getMessage());
        }
    }

    private String buildGenericExtractionPrompt() {
        return """
        Du bist ein Daten-Extraktionssystem für Versicherungsberatung.
        Analysiere die neue Kundennachricht und vergleiche sie mit bereits bekannten Daten.

        Antworte NUR im JSON-Array-Format:
        [
          {"key": "Name", "value": "Wert", "deleted": false},
          {"key": "Name", "value": null, "deleted": true}
        ]

        JSON-Regeln:
        - "key": Der technische Name des Feldes.
        - "value": Der extrahierte Wert. Bei Löschung kann dieser 'null' sein.
        - "deleted": Setze dieses Feld auf 'true', wenn der Nutzer eine Information explizit zurücknimmt. Ansonsten immer 'false'.

        Extraktion-Regeln:
            - Wenn ein Wert bereits bekannt und unverändert ist → NICHT erneut zurückgeben.
            - Wenn ein Wert sich geändert hat → den neuen Wert zurückgeben.
            - Wenn eine neue Information erkannt wird → zurückgeben.
            - Wenn keine neuen oder geänderten Daten gefunden werden → gib [] zurück.
        """;
    }

    private String buildSpecificExtractionPrompt() {
        // Todo: Load list from config
        return """
                Folgende Daten sollen extrahiert werden (in Anführungszeichen steht der Key-Name:
                - "name"
                - "geburtsdatum"
                - "adresse"
                - "familienstand" (ledig, verheiratet, etc.)
                - "anzahl_kinder" (als Int angeben von 0 bis ...)
                - "beruf"
                - "hobbys"
                """;
    }

    private String buildKnownDataJson(List<ExtractedDataEntry> knownData) {
        try {
            return new ObjectMapper().writeValueAsString(knownData);
        } catch (JsonProcessingException e) {
            log.error("Fehler beim Serialisieren der bekannten Daten: {}", e.getMessage());
            return "[]";
        }
    }
}
