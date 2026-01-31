package it.gael.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.gael.npc.network.BotServer;
import org.java_websocket.WebSocket;

// PACKAGE REALI DAL TUO JAR (Sanasol/Hypixel)
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.commandsystem.basecommands.AbstractPlayerCommand;

// Nota: Alcuni import specifici (come gli Eventi) potrebbero variare leggermente.
// Per ora abilitiamo solo il comando e il WebSocket per testare la build.

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NPCPlugin extends JavaPlugin {

    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting NPC Brain Bridge on port 8080...");
        
        // 1. Avvia il Server WebSocket (Il telefono per il cervello Python)
        server = new BotServer(new InetSocketAddress(8080));
        server.start();
        
        // 2. Registra il comando /spawnnpc
        this.getCommandManager().registerCommand(new SpawnCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            if (server != null) server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NPCPlugin getInstance() {
        return instance;
    }
    
    // ==========================================
    // Classe Comando Interna
    // ==========================================
    public static class SpawnCommand extends AbstractPlayerCommand {
        private final NPCPlugin plugin;

        public SpawnCommand(NPCPlugin plugin) {
            // Nome, Descrizione, Permesso, Alias
            super("spawnnpc", "Spawna un NPC AI", null, "npc");
            this.plugin = plugin;
        }

        @Override
        public void execute(Player player, String[] args) {
            if (args.length < 1) {
                player.sendMessage("Usa: /spawnnpc <nome>");
                return;
            }
            String name = args[0];
            player.sendMessage("Sto evocando " + name + " (Logica WIP)...");
            
            // Qui andrà il codice di spawn reale appena confermiamo che compila
            plugin.getLogger().info("Player " + player.getName() + " spawned " + name);
        }
    }
}
