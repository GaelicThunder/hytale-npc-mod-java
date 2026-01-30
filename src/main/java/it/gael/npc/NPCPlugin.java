package it.gael.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.gael.npc.network.BotServer;
import net.hytalegame.api.HytalePlugin; // Mock API Package
import net.hytalegame.api.event.Subscribe;
import net.hytalegame.api.event.player.PlayerChatEvent;
import net.hytalegame.api.event.server.ServerTickEvent;
import net.hytalegame.api.world.World;
import net.hytalegame.api.entity.Player;
import net.hytalegame.api.entity.Entity;

import java.net.InetSocketAddress;
import java.util.Collection;

public class NPCPlugin extends HytalePlugin {

    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting NPC Brain Bridge on port 8080...");
        
        // Start WebSocket Server
        server = new BotServer(new InetSocketAddress(8080));
        server.start();
        
        getLogger().info("Waiting for Brain connection...");
    }

    @Override
    public void onDisable() {
        try {
            if (server != null) server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // Event Listeners (Hooking into Game)
    // ==========================================

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        // Send chat to Python Brain
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("sender", player.getName());
        json.addProperty("message", message);
        
        // Gather Context (Position, Health, Nearby Mobs)
        JsonObject context = new JsonObject();
        context.addProperty("health", player.getHealth());
        context.addProperty("pos", player.getPosition().toString()); // Assuming Vector3.toString() exists
        context.addProperty("time", player.getWorld().getTime());
        
        // Scan nearby entities (Simulated API call)
        // context.add("nearby", getNearbyEntitiesJson(player));

        json.add("context", context);
        
        // Broadcast to all connected Brains (usually just one)
        server.broadcast(gson.toJson(json));
    }

    // Helper to execute commands received from Python
    public void executeNPCAction(String npcName, String command, String targetName, String speech) {
        // This runs on the main server thread
        
        // 1. Handle Speech
        if (speech != null && !speech.isEmpty()) {
            getServer().broadcastMessage("[" + npcName + "]: " + speech);
        }

        // 2. Handle Movement/Action
        if (command != null) {
            switch (command) {
                case "FOLLOW":
                    Player target = getServer().getPlayer(targetName);
                    if (target != null) {
                        // Logic to make NPC follow target
                        // NPCManager.get(npcName).getNavigator().setTarget(target);
                        getLogger().info("NPC " + npcName + " is following " + targetName);
                    }
                    break;
                case "ATTACK":
                     // Logic to attack
                     break;
            }
        }
    }
    
    public static NPCPlugin getInstance() {
        return instance;
    }
}
