package de.adesso.ai.digitalerberater.bot.service.looks_like_util_and_or_config;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AnalyzeService {

    public List<String> analyzeInsuranceSubCategory(String text) {
        List<String> versicherungMap = new ArrayList<>();

        // Pattern für komplette Einträge: Nummer. **Name**: Beschreibung
        Pattern pattern = Pattern.compile(
                "\\d+\\.\\s*\\*\\*([^*]+)\\*\\*:\\s*([^\\d]+?)(?=\\d+\\.|$)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String name = matcher.group(1).trim();
            versicherungMap.add(name);
        }

        return versicherungMap;
    }

    private String cleanAndValidateInsurance(String insurance) {
        if (insurance == null) return null;

        // Bereinigung: Entferne führende/trailing Sonderzeichen und überflüssige Leerzeichen
        insurance = insurance
                .replaceAll("^[^A-Za-zäöüß]+", "")
                .replaceAll("[^A-Za-zäöüß]+$", "")
                .replaceAll("\\s+", " ")
                .trim();

        // Validierung: Mindestlänge und muss "versicherung" enthalten
        if (insurance.length() < 5 || !insurance.toLowerCase().contains("versicherung")) {
            return null;
        }

        return insurance;
    }

    public List<String> analyzeInsurance(String aiResponse) {
        if (aiResponse == null || aiResponse.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> versicherungsListe = new ArrayList<>();

        String normalizedResponse =
                aiResponse.toLowerCase().replaceAll("[^a-zäöüß\\s-]", " ").replaceAll("\\s+", " ");

        VERSICHERUNGS_KEYWORDS.forEach((versicherungstyp, keywords) -> {
            List<String> gefundeneKeywords = keywords.stream()
                    .filter(keyword -> containsKeyword(normalizedResponse, keyword))
                    .toList();

            if (!gefundeneKeywords.isEmpty()) {
                versicherungsListe.add(versicherungstyp);
            }
        });

        return versicherungsListe;
    }

    // Umfassende Liste von Versicherungsbegriffen
    private static final Map<String, Set<String>> VERSICHERUNGS_KEYWORDS = Map.of(
            "Haftpflichtversicherung",
                    Set.of(
                            "haftpflicht",
                            "haftpflichtversicherung",
                            "privathaftpflicht",
                            "phv",
                            "private haftpflicht",
                            "private haftpflichtversicherung",
                            "haftpflichtversicherungen"),
            "Krankenversicherung",
                    Set.of(
                            "krankenversicherung",
                            "krankenkasse",
                            "gesetzliche krankenversicherung",
                            "private krankenversicherung",
                            "pkv",
                            "gkv",
                            "zusatzversicherung"),
            "Lebensversicherung",
                    Set.of(
                            "lebensversicherung",
                            "kapitallebensversicherung",
                            "risikolebensversicherung",
                            "rentenversicherung",
                            "private rentenversicherung"),
            "Hausratversicherung", Set.of("hausrat", "hausratversicherung", "inventarversicherung"),
            "Wohngebäudeversicherung",
                    Set.of(
                            "wohngebäude",
                            "wohngebäudeversicherung",
                            "gebäudeversicherung",
                            "feuerversicherung",
                            "sturmversicherung",
                            "leitungswasser"),
            "Kfz-Versicherung",
                    Set.of(
                            "kfz-versicherung",
                            "autoversicherung",
                            "kraftfahrzeugversicherung",
                            "vollkasko",
                            "teilkasko"),
            "Berufsunfähigkeitsversicherung",
                    Set.of(
                            "berufsunfähigkeit",
                            "berufsunfähigkeitsversicherung",
                            "bu-versicherung",
                            "erwerbsunfähigkeitsversicherung"),
            "Rechtsschutzversicherung", Set.of("rechtsschutz", "rechtsschutzversicherung"),
            "Unfallversicherung", Set.of("unfallversicherung", "private unfallversicherung"),
            "Reiseversicherung",
                    Set.of(
                            "reiseversicherung",
                            "reisekrankenversicherung",
                            "reiserücktrittsversicherung",
                            "auslandsreiseversicherung"));

    private boolean containsKeyword(String text, String keyword) {
        // Pattern für Wortgrenzen erstellen
        String pattern = "\\b" + Pattern.quote(keyword) + "\\b";
        return Pattern.compile(pattern).matcher(text).find();
    }
}
