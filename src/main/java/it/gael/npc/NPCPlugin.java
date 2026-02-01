package it.gael.npc;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class NPCPlugin extends JavaPlugin {

    public NPCPlugin(JavaPluginInit init) {
        super(init);
        System.out.println("[NPCPlugin] Costruttore chiamato - Plugin istanziato");
    }

    @Override
    public void setup() {
        System.out.println("[NPCPlugin] setup() ESEGUITO! Il plugin è attivo e funzionante.");
        
        // Qui possiamo far partire il server ZMQ/WebSocket in un thread separato
        /*
        new Thread(() -> {
            try {
                // start server...
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        */
    }
}
