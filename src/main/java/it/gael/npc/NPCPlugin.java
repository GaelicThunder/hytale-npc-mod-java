package it.gael.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.gael.npc.network.BotServer;
import net.hytalegame.api.HytalePlugin; // Mock API
import net.hytalegame.api.event.Subscribe;
import net.hytalegame.api.event.player.PlayerChatEvent;
import net.hytalegame.api.command.CommandSender;
import net.hytalegame.api.command.Command;
import net.hytalegame.api.entity.Player;
import net.hytalegame.api.entity.Npc;
import net.hytalegame.api.world.Location;
import net.hytalegame.api.world.Block;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class NPCPlugin extends HytalePlugin {

    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;
    private final Map<String, Npc> activeNPCs = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Starting NPC Brain Bridge on port 8080...");
        server = new BotServer(new InetSocketAddress(8080));
        server.start();
        registerCommand("spawnnpc", this::onSpawnCommand);
    }

    @Override
    public void onDisable() {
        if (server != null) try { server.stop(); } catch (Exception e) {}
    }

    public boolean onSpawnCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player) || args.length < 1) return false;
        spawnNPC(((Player) sender).getLocation(), args[0]);
        return true;
    }

    public void spawnNPC(Location location, String name) {
        Npc npc = location.getWorld().spawn(location, Npc.class);
        npc.setName(name);
        activeNPCs.put(name, npc);
    }

    @Subscribe
    public void onPlayerChat(PlayerChatEvent event) {
        if (activeNPCs.isEmpty()) return;
        Player p = event.getPlayer();
        
        JsonObject json = new JsonObject();
        json.addProperty("type", "chat");
        json.addProperty("sender", p.getName());
        json.addProperty("message", event.getMessage());
        
        JsonObject context = new JsonObject();
        context.addProperty("health", p.getHealth());
        context.addProperty("pos", p.getLocation().toString());
        // TODO: Scan nearby entities and add to JSON
        // context.add("nearby_entities", scanNearbyEntities(p));
        
        json.add("context", context);
        server.broadcast(gson.toJson(json));
    }

    public void executeNPCAction(String npcName, String command, String targetName, String speech) {
        Npc npc = activeNPCs.get(npcName);
        if (npc == null) return;

        if (speech != null && !speech.isEmpty()) {
            getServer().broadcastMessage("[" + npcName + "]: " + speech);
        }

        if (command != null) {
            switch (command) {
                case "FOLLOW":
                    Player target = getServer().getPlayer(targetName);
                    if (target != null) npc.getNavigator().setTarget(target);
                    break;
                case "ATTACK":
                    // Logic to find entity by name/type and attack
                    // Entity enemy = findEntityNearby(npc, targetName);
                    // if (enemy != null) npc.setTarget(enemy);
                    break;
                case "FIND":
                    // Scan radius for block type
                    // Block b = findNearestBlock(npc.getLocation(), targetName); // e.g., "Copper Ore"
                    // if (b != null) executeNPCAction(npcName, "GOTO", b.getLocation().toString(), "Found it!");
                    break;
                case "MINE":
                    // Break block at target location
                    // Block b = parseLocation(targetName);
                    // npc.lookAt(b);
                    // npc.swingArm();
                    // b.breakNaturally();
                    break;
                case "GOTO":
                    // npc.getNavigator().setTarget(parseLocation(targetName));
                    break;
            }
        }
    }
    
    public static NPCPlugin getInstance() { return instance; }
    private void registerCommand(String name, CommandExecutor executor) {}
    interface CommandExecutor { boolean onCommand(CommandSender s, Command c, String l, String[] a); }
}
