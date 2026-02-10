# Rasa Service

This repository contains a Rasa Chatbot for our 'Digitaler Berater'.

### 🚀 Setup

1. Install the following requirements correctly:
    - [Python 3.9](https://www.python.org/downloads/)
    - [Poetry](https://www.python-poetry.org/): For Python packaging and dependency management ([Installation Instruction](https://python-poetry.org/docs/#installation)).

2. Install Dependencies:

    - On first installation of dependencies, first a poetry environment must be created. Locate the installed Python version and run:
        ```sh
        poetry env use "C:path\to\3.9\python.exe"
        ```

    - To install all dependencies and ensure they match the `pyproject.toml` and `poetry.lock` files, run:
        ```sh
        poetry install
        ```

    - Optionally: Set the VSCode Python environment to the one created (may require restarting the IDE)

### 🚀 Running Rasa with Docker Compose

1. Build the Docker images as named in the `docker-compose.shell.yml` file.
2. Start the services in detached mode by running:
    ```sh
    docker compose up -d
    ```
3. Attach to the rasa-service container to interact with the Rasa shell:
    ```sh
    docker attach rasa
    ```

### 🚀 Running Rasa (without docker)
1. Navigate to subddirectory `rasa_bot`
    ```sh
    cd rasa_bot
    ```
2. Execute the following  commands to train or run rasa:
    - Train a model if not already there in `rasa_bot/models/`:
        ```sh
        poetry run rasa train --domain domain --data data
        ```
    - Run these two commands in two shells to run the Rasa for testing in commandline:
        ```sh
        poetry run rasa run actions
        ```
        ```
        poetry run rasa shell --cors "*" --debug --endpoint endpoints.yml
        ```
    - Run these two commands in two shells to run the Rasa API for FrontEnd:
        ```sh
        poetry run rasa run actions
        ```
        ```
        poetry run rasa run --enable-api --cors "*" --debug --endpoint endpoints.yml
        ```
3. The first Client-Message should be `/start_conversation`, which will triger the initial chatbot message.

### 🧪 Running Rasa Tests

1. Navigate to subddirectory `rasa_bot`
    ```sh
    cd rasa_bot
    ```
2. To run all tests locally, simply execute:
    ```bash
    poetry run rasa test --stories tests -d domain -c config.yml
    ```
    OR
    ```bash
    poetry run rasa test core --stories tests
    ```

### 🛠️ Running Pre-commit-hooks

1. To run the linters and formatters specified in your pre-commit hooks, simply execute:
    ```bash
    poetry run pre-commit run --all-files
    ```

# Rasa Grundlagen

## Grundlegende Befehle
ACHTUNG: Befehle müssen idR im Verzeichnis ..\rasa_bot ausgeführt werden.
- **`rasa -h`**: Essentielle Rasa-Befehle und ihre Erklärung
- **`rasa run --enable-api -cors "*" -debug`**: Startet den Server, der es ermöglicht, dass dein Modell über HTTP-Anfragen (z.B. über das Angular Frontend) erreicht wird.
- **`rasa shell`**: Startet eine interaktive Konsole, in der du mit dem Rasa-Modell direkt sprechen kannst.
- **`rasa interactive --domain domain --data data`**: : Startet eine interaktive Konsole, in der du mit dem Rasa-Modell direkt sprechen kannst. Allerdings mit mehr Informationen!
- **`rasa train --domain domain --data data`**: Trainiert das Modell mit den aktuellen Daten aus den Konfigurationsdateien und den Trainingsdaten.
- **`rasa run actions`**: Startet einen Server, der verwendet wird, um benutzerdefinierte Aktionen (Custom Actions) auszuführen.
- **`rasa shell nlu`**: NLU-Funktionalität des Modells testen für Intents/Entities
- **`rasa data validate`**: Validierung der Daten

## Grundlegende Rasa-Konzepte

- **Intent**: Definiert die Absicht des Benutzers, die durch die Eingabe ausgedrückt wird.

- **Response / Action**: In Rasa bezeichnet man Reaktionen auf Intents als `actions`. Diese können sowohl einfache Antworten als auch komplexe benutzerdefinierte Aktionen umfassen.

- **Story**: Ein Dialogfluss oder eine Abfolge von Interaktionen, die Rasa verwendet, um die möglichen Konversationen zu lernen und zu verstehen.

- **Rule**: Definiert spezifische Regeln für Dialogverläufe, die unabhängig von der Story immer gelten. Zum Beispiel wird eine Begrüßung immer mit einer bestimmten Antwort beantwortet.

- **Entity**: Daten oder Informationen, die aus den Benutzereingaben extrahiert werden. Sie werden in `nlu.yml` und `domain.yml` definiert.

- **Slot**: Speicher für Informationen, die während der Konversation gesammelt werden. Sie sind Schlüssel-Wert-Paare, die während der Konversation gespeichert und verwendet werden.

## Dateien und deren Inhalte

- **`nlu.yml`**: Enthält die Definition der Intents und die dazugehörigen Trainingsbeispiele.

- **`domain.yml`**: Alles was der Bot weiß. Definiert Intents, Entities, Slots, Responsens, Forms und Actions. Hier wird auch das Verhalten des Modells konfiguriert.

- **`stories.yml`**: Definiert Dialogverläufe und Interaktionsströme zwischen Intents, Actions und Slots. So werden mehrstufige Konversationen modelliert, bei dem der Verlauf der Interaktion je nach vorherigem Kontext/Usereingaben variieren kann und so eine dynamische Entscheidung erfolgen kann.

- **`rules.yml`**: Definiert Regeln für Dialogverläufe bei statischen/vorhersehbaren Situationen, in denen der Bit immer gleich reagieren soll, unabhängig vom Konversationsverlauf/Kontext. (z.B. Begrüßung)


## Weitere Infos
- Siehe Dokumentation im [Confluence](https://confluence.adesso.de/pages/viewpage.action?pageId=660932852).
