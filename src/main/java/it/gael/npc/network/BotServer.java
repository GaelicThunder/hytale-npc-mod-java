package it.gael.npc.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.gael.npc.NPCPlugin;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class BotServer extends WebSocketServer {

    public BotServer(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Brain Connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Brain Disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Received command from Python Brain
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String type = json.get("type").getAsString();

            if ("npc_command".equals(type)) {
                String command = json.has("command") ? json.get("command").getAsString() : "IDLE";
                String target = json.has("target") && !json.get("target").isJsonNull() ? json.get("target").getAsString() : null;
                String chat = json.has("chat") ? json.get("chat").getAsString() : "";

                // Dispatch to Main Game Thread (Crucial for thread safety in games)
                // Assuming Hytale API has a Scheduler
                // Bukkit.getScheduler().runTask(...) equivalent
                
                NPCPlugin.getInstance().executeNPCAction("Gillian", command, target, chat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server started successfully");
    }
}
