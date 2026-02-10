## Setup

### 1. Datenbank starten

Docker Compose ausführen, um die Datenbank zu starten:

docker compose up

### 2. OpenAI/LLM API-Key setzen

Ersetze `<DEIN_API_KEY>` durch einen API-Schlüssel mit folgendem Befehl: 

**Für Windows (PowerShell):**

setx OPENAI_API_KEY "<DEIN_API_KEY>"

**Für macOS / Linux (Bash):**

export OPENAI_API_KEY="<DEIN_API_KEY>"

### 3. Azure Endpoint-Info setzen

Wenn du ein Modell über Azure OpenAI verwendest, müssen zusätzlich zum API-Key der Endpoint gesetzt werden.

**Für Windows (PowerShell):**

setx OPENAI_API_ENDPOINT "<DEIN_API_ENDPOINT>"

**Für macOS / Linux (Bash):**

export OPENAI_API_ENDPOINT="<DEIN_API_ENDPOINT>"

### Tipp

Wenn ein error erscheint bei der Eingabe im Chat, starte die IDE neu/öffne ein neues Powershell-Fenster

---

## Anwendung starten

### 1. Backend starten

Die Hauptklasse `Application.java` ausführen

### 2. Benutzeroberfläche aufrufen

Die Weboberfläche ist unter folgender Adresse erreichbar:

http://localhost:8080/thymeleaf/conversation

## Entwicklung

### Code-Formatierung
Zur automatisierten Formatierung des Codes sollte folgender Befehl verwendet werden:

./mvnw.cmd spotless:apply (Windows)

## Hinweise

### Falls ein Fehler wegen falscher Vektordimension auftritt 

**Erklärung:**

Wenn die in der Datenbank festgelegte Vektorgröße nicht mit der tatsächlichen Embedding-Dimension übereinstimmt, kommt es zu einem Fehler beim Einfügen.

**Befehle zum Anpassen:**

1. Zum Öffnen der Posgre-Konsole im Docker-Container: 
   
docker exec -it postgres-db psql -U pi -d example_db

1. Passe die Dimension der Spalte an mit: 

ALTER TABLE public.vector_store  
ALTER COLUMN embedding TYPE vector(<EMBEDDING_DIMENSION>);

Ersetze <EMBEDDING_DIMENSION> durch die tatsächliche Dimension des von dir verwendeten Embedding-Modells, z. B. 1536 für OpenAI text-embedding-ada-002.
