# Hytale NPC Mod (Java) - WebSocket Bridge

Mod server-side per Hytale che abilita il controllo degli NPC tramite intelligenza artificiale esterna (Python Brain).

## ⚠️ Requisiti Fondamentali

1.  **Java JDK 11+**
2.  **Gradle** (Sistema di build)
3.  **Server Jar**: Devi avere il file `.jar` del tuo server Hytale (es. Sanasol o official leak).

## 🛠️ Setup e Compilazione

### 1. Preparazione Librerie
Crea una cartella `libs` nella root del progetto e copiaci dentro il jar del server:
```bash
mkdir libs
cp /path/to/your/hytale-server.jar libs/
```

### 2. Compilazione (Arch Linux)
Dato che il wrapper (`gradlew`) potrebbe mancare del file binario `.jar` nella repo, usa il Gradle di sistema:

```fish
# Installa Gradle (se non ce l'hai)
sudo pacman -S gradle

# Compila il progetto
gradle shadowJar
```

Il file compilato sarà in: `build/libs/hytale-npc-mod-java-1.0.0.jar`.

### 3. Installazione
Sposta il file `.jar` generato nella cartella `plugins/` (o `mods/`) del tuo server Hytale.

## 🚀 Comandi In-Game

- `/spawnnpc <nome>`: Spawna un NPC controllato dall'AI nella tua posizione.
  - Esempio: `/spawnnpc Gillian`

## Architettura
Questa mod apre un Server WebSocket sulla porta **8080**.
Quando un evento (Chat) accade vicino a un NPC registrato, invia un JSON al "Cervello" Python.
Riceve comandi JSON (`FOLLOW`, `MINE`, `ATTACK`) ed esegue le azioni di gioco.
