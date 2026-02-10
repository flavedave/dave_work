# Digitaler Berater - Vorher / Nachher
## Folieninhalte fuer die Praesentation

---

## FOLIE 1: Rasa - Was konnte der Digitale Berater?

**Ausgangslage: Regelbasierter Chatbot mit Rasa-Framework**

- **3 Versicherungsprodukte** abgebildet: Private Haftpflicht, Hausrat, KFZ
- **Gefuehrte Tarifberechnung**: Der Bot fuehrt Kunden Schritt fuer Schritt durch einen Fragenkatalog (z. B. Wohnflaeche, Geburtsdatum, Beruf, Selbstbeteiligung)
- **Button-basierte Interaktion**: Antwortmoeglichkeiten werden dem Kunden als Auswahlbuttons angeboten (z. B. "Einfamilienhaus / Zweifamilienhaus / Wohnung")
- **Eingabevalidierung**: Geburtsdatum (Mindestalter 18), Versicherungsbeginn (muss in der Zukunft liegen), Fahrzeugdaten (HSN/TSN)
- **Aenderungen waehrend der Eingabe**: Kunde kann bereits gemachte Angaben korrigieren, ohne von vorne beginnen zu muessen
- **FAQ-Beantwortung**: Allgemeine Versicherungsfragen (Was deckt die Versicherung ab? Wann kann ich kuendigen?) werden ueber eine angebundene KI beantwortet
- **Kontextuelle Erklaerungen**: Bei jeder Frage kann der Kunde nachfragen "Warum braucht ihr das?" und erhaelt eine verstaendliche Erklaerung

---

## FOLIE 2: Rasa - Wo waren die Grenzen?

**Starre Gespraeche, hoher Pflegeaufwand, begrenzte Intelligenz**

- **Jeder Gespraechsverlauf musste vorab definiert werden**
  Neue Fragen, neue Produkte oder geaenderte Ablaeufe erforderten manuelle Anpassungen an mehreren Stellen gleichzeitig (Trainingsdaten, Dialogregeln, Validierungslogik, Antworten)

- **Hunderte Formulierungsvarianten manuell trainiert**
  Damit der Bot "Ich moechte meine Angabe zum Beruf aendern" versteht, mussten 300+ Beispielsaetze von Hand geschrieben werden - fuer jede einzelne Absicht

- **Keine echte Beratung, nur Formular-Abarbeitung**
  Der Bot hat Fragen der Reihe nach gestellt und Antworten gesammelt - aber er konnte nicht erklaeren, *warum* ein bestimmter Tarif besser passt oder was bei einem Schadensfall zu tun ist

- **Neue Versicherungsprodukte = Wochen Aufwand**
  Ein neues Produkt (z. B. Rechtsschutz, Berufsunfaehigkeit) erforderte: neue Trainingsdaten, neue Dialogregeln, neue Formulare, neue Validierungslogik, neue Antwort-Texte - alles manuell

- **KFZ-Sparte nur als Prototyp**
  Das KFZ-Formular war unvollstaendig - viele Felder (Fahrleistung, Nutzungsart, Fahrerkreis, Wohnort) waren bereits geplant, aber noch nicht umgesetzt

- **Schwierigkeiten mit freier Texteingabe**
  Wenn der Kunde nicht exakt das geschrieben hat, was der Bot erwartet, kam es zu Missverstaendnissen oder Rueckfragen ("Das habe ich leider nicht verstanden")

- **Abhaengigkeit von einem Nischen-Framework**
  Rasa als Open-Source-Projekt mit unsicherer Zukunft: eingeschraenkter Support, steigende Komplexitaet, schwierige Weiterentwicklung

---

## FOLIE 3: Spring AI - Was kann der Digitale Berater jetzt?

**LLM-basierter Berater mit echtem Sprachverstaendnis**

- **Natuerliche Gespraechsfuehrung statt starrer Formulare**
  Der Kunde kann in eigenen Worten beschreiben, was er braucht - das System versteht die Absicht, ohne auf vordefinierte Saetze angewiesen zu sein

- **Echte Beratungskompetenz**
  Der Bot kann Tarife erklaeren, Empfehlungen aussprechen und Fachfragen beantworten - nicht nur Daten abfragen, sondern aktiv beraten

- **Neue Produkte in Stunden statt Wochen**
  Erweiterung um neue Versicherungsprodukte durch Anpassung der Wissensbasis und Prompts - kein aufwaendiges Training von Modellen noetig

- **Freie Texteingabe ohne Einschraenkungen**
  Kunden koennen schreiben wie sie moechten - Tippfehler, Umgangssprache und komplexe Saetze werden zuverlaessig verstanden

- **Kontextuelles Gedaechtnis**
  Der Bot merkt sich den Gespraechsverlauf und kann Rueckfragen sinnvoll einordnen ("Sie sagten vorhin Einfamilienhaus - moechten Sie dazu auch eine Glasversicherung?")

- **Enterprise-faehige Plattform**
  Spring Boot als bewaehrtes Java-Oekosystem: einfache Integration in bestehende IT-Landschaften, langfristiger Support, grosse Entwickler-Community

- **RAG-Anbindung an Unternehmenswissen**
  Versicherungsbedingungen, Produktblaetter und FAQs werden automatisch als Wissensquelle eingebunden - der Bot antwortet auf Basis aktueller Dokumente

---

## FOLIE 4: Der Vergleich auf einen Blick

| Kriterium | Rasa (vorher) | Spring AI (jetzt) |
|---|---|---|
| **Gespraechsfuehrung** | Feste Ablaeufe, Button-gesteuert | Frei, natuerlich, kontextbezogen |
| **Sprachverstaendnis** | Musterabgleich auf Trainingsdaten | LLM mit echtem Sprachverstaendnis |
| **Beratungstiefe** | Formular-Abfrage | Aktive Empfehlung und Erklaerung |
| **Neues Produkt hinzufuegen** | Wochen (Daten, Regeln, Code) | Stunden (Wissensbasis, Prompt) |
| **Freie Texteingabe** | Fehleranfaellig | Robust und flexibel |
| **Wartungsaufwand** | Hoch (mehrere Konfigurationsebenen) | Gering (zentralisierte Steuerung) |
| **Technologische Basis** | Python/Rasa (Nische) | Java/Spring (Enterprise-Standard) |
| **Zukunftssicherheit** | Unsicher (Rasa-Roadmap) | Hoch (Spring-Oekosystem) |
| **Skalierbarkeit** | Aufwaendig | Cloud-native, skalierbar |

---

### Hinweise fuer den Vortrag

**Kernbotschaft Folie 1+2:** "Mit Rasa hatten wir einen funktionierenden Prototyp - aber jeder neue Anwendungsfall erforderte wochenlanges manuelles Engineering. Der Bot konnte Formulare ausfuellen, aber nicht beraten."

**Kernbotschaft Folie 3+4:** "Mit Spring AI und LLMs haben wir den Sprung von einem regelbasierten Formular-Bot zu einem intelligenten Berater geschafft. Der Kunde merkt den Unterschied sofort: natuerliche Sprache, echte Antworten, keine Sackgassen."

**Empfohlene Demo-Szenarien:**
1. Gleiche Frage in beiden Systemen stellen (z. B. "Ich hab ne Wohnung mit 80qm und wuerde gern wissen was mich das kostet") - zeigt den Unterschied in der Sprachverarbeitung
2. Fachfrage stellen (z. B. "Lohnt sich eine Glasversicherung bei meinem Altbau?") - zeigt Beratungskompetenz vs. Formular-Logik
3. Produktwechsel mitten im Gespraech (z. B. von Hausrat auf Haftpflicht) - zeigt Flexibilitaet vs. starre Ablaeufe
