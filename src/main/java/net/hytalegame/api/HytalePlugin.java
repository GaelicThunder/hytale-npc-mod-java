package net.hytalegame.api;

// MOCK CLASS FOR COMPILATION
// Remove this when compiling against real server jar

import net.hytalegame.api.event.Event;

public class HytalePlugin {
    public void onEnable() {}
    public void onDisable() {}
    public Logger getLogger() { return new Logger(); }
    public Server getServer() { return new Server(); }
    
    public static class Logger {
        public void info(String s) { System.out.println("[INFO] " + s); }
    }
    
    public static class Server {
        public void broadcastMessage(String s) { System.out.println("[CHAT] " + s); }
        public net.hytalegame.api.entity.Player getPlayer(String name) { return new net.hytalegame.api.entity.Player(name); }
    }
}
