package it.gael.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.gael.npc.network.BotServer;
import net.hytalegame.api.HytalePlugin; // Mock API
import net.hytalegame.api.event.Subscribe;
import net.hytalegame.api.event.player.PlayerChatEvent;
import net.hytalegame.api.event.player.PlayerJoinEvent;
import net.hytalegame.api.command.CommandSender;
import net.hytalegame.api.command.Command;
import net.hytalegame.api.entity.Player;
import net.hytalegame.api.entity.Npc;
import net.hytalegame.api.world.Location;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NPCPlugin extends HytalePlugin {

    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;
    
    // Store active NPCs: Name -> NpcObject
    private final Map<String, Npc> activeNPCs = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting NPC Brain Bridge on port 8080...");
        
        // Start WebSocket Server
        server = new BotServer(new InetSocketAddress(8080));
        server.start();
        
        // Register Command (Mock logic - usually via plugin.yml or command manager)
        registerCommand("spawnnpc", this::onSpawnCommand);
        
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
    // Commands
    // ==========================================
    
    public boolean onSpawnCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can spawn NPCs.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /spawnnpc <name>");
            return true;
        }

        Player player = (Player) sender;
        String npcName = args[0];
        
        spawnNPC(player.getLocation(), npcName);
        player.sendMessage("Spawned NPC: " + npcName);
        return true;
    }

    public void spawnNPC(Location location, String name) {
        // Logic to actually spawn the entity in the world
        // This heavily depends on the specific Server API you are using
        
        // Example logic:
        Npc npc = location.getWorld().spawn(location, Npc.class);
        npc.setName(name);
        npc.setSkin("default_female"); // Example skin
        
        activeNPCs.put(name, npc);
        getLogger().info("Created NPC instance for " + name);
    }

    // ==========================================
    // Event Listeners
    // ==========================================

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Don't process chat if no NPCs are around (optional optimization)
        if (activeNPCs.isEmpty()) return;

        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("sender", player.getName());
        json.addProperty("message", message);
        
        JsonObject context = new JsonObject();
        context.addProperty("health", player.getHealth());
        context.addProperty("pos", player.getLocation().toString());
        context.addProperty("time", player.getWorld().getTime());
        
        json.add("context", context);
        server.broadcast(gson.toJson(json));
    }

    // ==========================================
    // Action Execution
    // ==========================================

    public void executeNPCAction(String npcName, String command, String targetName, String speech) {
        Npc npc = activeNPCs.get(npcName);
        if (npc == null) {
            getLogger().warning("Received command for unknown NPC: " + npcName);
            return;
        }

        // 1. Handle Speech (Chat Bubble or Server Chat)
        if (speech != null && !speech.isEmpty()) {
            // Option A: Global Chat
            getServer().broadcastMessage("[" + npcName + "]: " + speech);
            // Option B: Floating text above head (if supported)
            // npc.showChatBubble(speech);
        }

        // 2. Handle Movement/Action
        if (command != null) {
            switch (command) {
                case "FOLLOW":
                    Player target = getServer().getPlayer(targetName);
                    if (target != null) {
                        npc.getNavigator().setTarget(target);
                        npc.lookAt(target.getLocation());
                    }
                    break;
                case "ATTACK":
                     // npc.setTarget(enemy);
                     break;
                case "GOTO":
                    // Parse target "x,y,z" and move
                    break;
            }
        }
    }
    
    public static NPCPlugin getInstance() {
        return instance;
    }
    
    // Mock helper for registration
    private void registerCommand(String name, CommandExecutor executor) {}
    interface CommandExecutor { boolean onCommand(CommandSender s, Command c, String l, String[] a); }
}
