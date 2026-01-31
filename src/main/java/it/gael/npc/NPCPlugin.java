package it.gael.npc;

import com.google.gson.Gson;
import it.gael.npc.network.BotServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.net.InetSocketAddress;

public class NPCPlugin extends JavaPlugin {

    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;

    // Costruttore richiesto da Hytale
    public NPCPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    // Rimosso @Override per sicurezza (se la firma cambia leggermente tra le versioni)
    public void onEnable() {
        System.out.println("[NPCPlugin] Brain Bridge Starting on Port 8080...");
        
        try {
            server = new BotServer(new InetSocketAddress(8080));
            server.start();
            System.out.println("[NPCPlugin] WebSocket Server Started!");
        } catch (Exception e) {
            System.err.println("[NPCPlugin] Error starting WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Rimosso @Override
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
    
    // Stub per azione NPC (da implementare con API reali quando disponibili)
    public void executeNPCAction(String npc, String cmd, String target, String chat) {
        System.out.println("BRAIN COMMAND: " + cmd + " -> " + target + " (Chat: " + chat + ")");
        // Qui andrà la logica di movimento usando this.getServer()...
    }
}
