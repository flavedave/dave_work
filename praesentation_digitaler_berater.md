# Digitaler Berater - Vorher / Nachher
## Folieninhalte fuer die Praesentation

---

## FOLIE 1: Rasa - Was konnte der Digitale Berater?

**Ausgangslage: Regelbasierter Chatbot mit Rasa-Framework (Python)**

- **3 Versicherungsprodukte** abgebildet: Private Haftpflicht (4 Fragen), Hausrat (10 Fragen), KFZ (4 Fragen)
- **Gefuehrte Tarifberechnung**: Der Bot fuehrt Kunden Schritt fuer Schritt durch einen festen Fragenkatalog (z. B. Wohnflaeche, Geburtsdatum, Beruf, Selbstbeteiligung)
- **Button-basierte Interaktion**: Antwortmoeglichkeiten werden als Auswahlbuttons angeboten (z. B. "Einfamilienhaus / Zweifamilienhaus / Wohnung", "Ja / Nein")
- **Eingabevalidierung**: Geburtsdatum (Mindestalter 18), Versicherungsbeginn (muss in der Zukunft liegen), Fahrzeugdaten (HSN/TSN)
- **Aenderung von Angaben**: Kunde kann bereits gemachte Angaben im Formular korrigieren, ohne von vorne beginnen zu muessen
- **FAQ-Beantwortung**: Allgemeine Versicherungsfragen ("Was deckt die Versicherung ab?", "Wann kann ich kuendigen?") werden ueber einen separaten KI-Dienst beantwortet
- **Kontextuelle Erklaerungen**: Bei jeder Frage kann der Kunde nachfragen "Warum braucht ihr das?" und erhaelt eine vorformulierte Erklaerung

---

## FOLIE 2: Rasa - Wo waren die Grenzen?

**Starre Gespraeche, hoher Pflegeaufwand, begrenzte Intelligenz**

- **Jeder Gespraechsverlauf musste vorab manuell definiert werden**
  8 Stories + 12 Rules + 23 Intents + 30 Slots + 6 Custom Actions - jede Aenderung erforderte Anpassungen an bis zu 5 Dateien gleichzeitig (Trainingsdaten, Dialogregeln, Validierungslogik, Domain, Antwort-Texte)

- **~1.000 Zeilen Trainingssaetze von Hand geschrieben**
  Damit der Bot eine einzige Absicht versteht (z. B. "Ich moechte meine Angabe zum Beruf aendern"), mussten Hunderte Formulierungsvarianten manuell erstellt werden - und trotzdem: bei unbekannten Formulierungen kam "Das habe ich leider nicht verstanden"

- **Keine echte Beratung - nur Formular-Abarbeitung**
  Der Bot hat Fragen der Reihe nach gestellt und Antworten gesammelt. Er konnte nicht erklaeren, warum ein bestimmter Tarif besser passt, keine Empfehlungen aussprechen und kein Gespraech fuehren

- **Neue Versicherungsprodukte = Wochen Aufwand**
  Ein neues Produkt (z. B. Rechtsschutz) erforderte: neue Trainingsdaten, neue Entitaeten, neue Slots, neue Dialogregeln, neue Formulare, neue Validierungslogik, neue Antwort-Texte

- **KFZ-Sparte nur als Prototyp**
  Von den geplanten 12 Feldern (Fahrleistung, Nutzungsart, Fahrerkreis, Wohnort, ...) waren nur 4 umgesetzt - der Rest war auskommentiert

- **Abhaengigkeit von einem Nischen-Framework**
  Rasa (Python 3.9) mit TensorFlow, spaCy, NLTK - schwer wartbar, eingeschraenkter Support, unklare Zukunft des Frameworks

---

## FOLIE 3: Spring AI - Was kann der Digitale Berater jetzt?

**LLM-basierter Berater mit Spring Boot, Azure OpenAI (GPT-4.1) und RAG**

- **Intelligente Gespraechsfuehrung in 7 Phasen**
  Der Bot fuehrt den Kunden durch einen natuerlichen Beratungsprozess: Begruessung → Bedarfsermittlung → Detailklaerung → Tarifempfehlung → Fragen beantworten → Auswahl bestaetigen → Zusammenfassung. Die Phasen werden dabei dynamisch vom LLM gesteuert - nicht starr abgearbeitet

- **Automatische Datenextraktion aus Freitext**
  Das LLM erkennt automatisch relevante Kundendaten (Name, Geburtsdatum, Adresse, Familienstand, Kinder, Beruf, Hobbys) aus natuerlicher Sprache - keine Buttons, keine festen Antwortformate noetig. Der Kunde sagt einfach "Ich bin Max, 35, verheiratet, zwei Kinder" und alle Daten werden erfasst

- **Echte Beratungskompetenz durch RAG**
  Versicherungsdokumente (DIN 77230, Produktinfos, Haftpflicht-Details) werden als Wissensbasis eingebunden. Der Bot antwortet auf Basis aktueller Unternehmens-Dokumente - nicht aus vorformulierten Textbausteinen

- **Neue Produkte in Stunden statt Wochen**
  Neues Produkt = neues Dokument in die Wissensbasis + Anpassung der Dialog-YAML-Konfiguration. Kein Training, kein neuer Code, keine neuen Regeln

- **Kontextuelles Gedaechtnis ueber das gesamte Gespraech**
  Chat-Historie wird mitgefuehrt (Windowed Truncation auf die letzten 10 Nachrichten). Der Bot kann Rueckbezuege herstellen und vorher genannte Informationen im Kontext nutzen

- **MCP-Schnittstelle (Model Context Protocol) vorbereitet**
  Anbindung externer Werkzeuge und Backends ueber das standardisierte MCP-Protokoll - z. B. fuer Tarifrechner, Bestandssysteme oder Vertragsverwaltung

- **Enterprise-Plattform mit Zukunftssicherheit**
  Spring Boot 3.5 / Java 21 / PostgreSQL mit pgvector / JWT-Authentifizierung / Swagger-API-Dokumentation - einfache Integration in bestehende Versicherungs-IT

---

## FOLIE 4: Der Vergleich auf einen Blick

| Kriterium | Rasa (vorher) | Spring AI (jetzt) |
|---|---|---|
| **Gespraechsfuehrung** | Feste Ablaeufe, Button-gesteuert | 7-phasiger Beratungsprozess, natuerliche Sprache |
| **Sprachverstaendnis** | Musterabgleich auf ~1.000 Trainingssaetzen | GPT-4.1 mit echtem Sprachverstaendnis |
| **Datenerfassung** | 30 vordefinierte Slots, einzeln abgefragt | Automatische Extraktion aus Freitext |
| **Beratungstiefe** | Formular-Abfrage ohne Erklaerung | Aktive Empfehlung auf Basis von Fachwissen (RAG) |
| **Wissensbasis** | Statische Antwort-Texte + separater FAQ-Dienst | RAG mit Vektordatenbank (pgvector), echte Dokumente |
| **Neues Produkt** | Wochen (Daten, Regeln, Slots, Code, Texte) | Stunden (Dokument + YAML-Config) |
| **Freie Texteingabe** | Fehleranfaellig, "nicht verstanden"-Meldungen | Robust - Tippfehler, Umgangssprache, komplexe Saetze |
| **Backend-Anbindung** | REST-Calls aus Python-Actions | MCP-Protokoll (standardisiert, erweiterbar) |
| **Technologische Basis** | Python 3.9, Rasa, TensorFlow, SQLite | Java 21, Spring Boot 3.5, PostgreSQL, Azure OpenAI |
| **Sicherheit** | Offene REST-Endpunkte | JWT-Authentifizierung, Spring Security |
| **Skalierbarkeit** | Docker Compose, monolithisch | Cloud-native, modular, skalierbar |
| **Zukunftssicherheit** | Rasa (Nische, unklare Roadmap) | Spring-Oekosystem (Enterprise-Standard) |

---

### Hinweise fuer den Vortrag

**Kernbotschaft Folie 1+2:** "Mit Rasa hatten wir einen funktionierenden Prototyp fuer 3 Produkte - aber jeder neue Anwendungsfall erforderte wochenlanges manuelles Engineering. Der Bot konnte Formulare ausfuellen, aber nicht beraten."

**Kernbotschaft Folie 3+4:** "Mit Spring AI und GPT-4.1 haben wir den Sprung vom regelbasierten Formular-Bot zum intelligenten Berater geschafft. Der Kunde schreibt frei, der Bot versteht, berät auf Basis echter Dokumente und fuehrt durch einen natuerlichen Beratungsprozess."

**Empfohlene Demo-Szenarien:**
1. **Freie Eingabe vs. Button-Zwang**: "Ich bin 35, verheiratet, zwei Kinder, wohne in einer 80qm-Wohnung in Hamburg" - Spring AI erfasst alle Daten auf einmal, Rasa haette 10 Einzelfragen gestellt
2. **Fachfrage stellen**: "Lohnt sich eine Glasversicherung bei meinem Altbau?" - Spring AI beantwortet aus der Wissensbasis, Rasa haette "Das habe ich leider nicht verstanden" gesagt oder an den FAQ-Dienst weitergeleitet
3. **Beratungstiefe zeigen**: "Welche Versicherungen brauche ich als Familie mit Eigenheim?" - Spring AI kann auf Basis der DIN 77230 eine priorisierte Empfehlung geben
4. **Themenwechsel**: Mitten im Gespraech von Hausrat auf Haftpflicht wechseln - Spring AI erkennt den neuen Topic automatisch, Rasa haette den Formular-Loop abgebrochen

### Zahlen fuer die Praesentation

| Kennzahl | Rasa | Spring AI |
|---|---|---|
| Zeilen Trainingsdaten | ~1.000 | 0 |
| Vordefinierte Dialogregeln | 20 (8 Stories + 12 Rules) | 0 (LLM-gesteuert) |
| Konfigurationsdateien fuer einen Dialog | 5+ (nlu, domain, stories, rules, actions) | 1 (dialogue.yaml) |
| Zeilen Code fuer Validierung | 515 (actions.py) | 0 (LLM validiert kontextbezogen) |
| Unterstuetzte Eingabeformate | Buttons + eingeschraenkter Freitext | Vollstaendig freier Text |
| Beratungsphasen | 3 (Frage → Antwort → Naechste Frage) | 7 (strukturierter Beratungsprozess) |
