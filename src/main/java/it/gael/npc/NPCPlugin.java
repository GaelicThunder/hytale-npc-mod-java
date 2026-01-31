package it.gael.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.gael.npc.network.BotServer;
import it.gael.npc.wrapper.MockHytaleAPI; // Wrapper interno

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

// Import Java-WebSocket
import org.java_websocket.WebSocket;

public class NPCPlugin {
    
    // NOTA PER GAËL:
    // Questa classe non estende HytalePlugin DIRETTAMENTE per evitare errori di compilazione
    // se le librerie non matchano.
    // Invece usiamo un approccio "Wrapper" dove tu dovrai solo collegare i fili.
    
    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;
    
    // Mappa semplice per tenere traccia degli NPC (Nome -> Oggetto Generico)
    private final Map<String, Object> activeNPCs = new HashMap<>();

    public void onEnable() {
        instance = this;
        System.out.println("Starting NPC Brain Bridge on port 8080...");
        
        server = new BotServer(new InetSocketAddress(8080));
        server.start();
        
        System.out.println("Waiting for Brain connection...");
    }

    public void onDisable() {
        try {
            if (server != null) server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // ESEMPIO DI GESTIONE CHAT (Da collegare al vero evento)
    // ==========================================
    public void handleChatEvent(String playerName, String message, Object playerEntity) {
        // Questa funzione deve essere chiamata dal TUO evento del server
        
        if (activeNPCs.isEmpty()) return;

        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("sender", playerName);
        json.addProperty("message", message);
        
        // Context Mock - Sostituisci con dati veri
        JsonObject context = new JsonObject();
        context.addProperty("health", 100); 
        context.addProperty("pos", "0,0,0"); // playerEntity.getLocation().toString()
        context.addProperty("time", "Day");
        
        json.add("context", context);
        server.broadcast(gson.toJson(json));
    }

    // ==========================================
    // ESECUZIONE AZIONI (Dalla AI)
    // ==========================================
    public void executeNPCAction(String npcName, String command, String targetName, String speech) {
        Object npc = activeNPCs.get(npcName);
        
        if (speech != null && !speech.isEmpty()) {
            // Esempio: Server.broadcastMessage("[" + npcName + "]: " + speech);
            System.out.println("NPC SAYS: " + speech);
        }

        if (command != null) {
            switch (command) {
                case "FOLLOW":
                    // Esempio: ((Npc) npc).getNavigator().setTarget(targetName);
                    System.out.println("NPC MOVING TO FOLLOW: " + targetName);
                    break;
                case "ATTACK":
                    System.out.println("NPC ATTACKING: " + targetName);
                    break;
                case "MINE":
                     System.out.println("NPC MINING: " + targetName);
                    break;
            }
        }
    }
    
    public static NPCPlugin getInstance() {
        return instance;
    }
}
