package it.gael.npc;

// import it.gael.npc.network.BotServer;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

public class NPCPlugin extends JavaPlugin {

    // private BotServer server;
    private static NPCPlugin instance;

    public NPCPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
        System.out.println("[NPCPlugin] Costruttore chiamato con successo!");
    }

    @Override
    public void onEnable() {
        System.out.println("[NPCPlugin] onEnable chiamato! HELLO WORLD!");
        
        /*
        try {
            System.out.println("[NPCPlugin] Provo a far partire il server (DISABILITATO)...");
            // server = new BotServer(new java.net.InetSocketAddress(8080));
            // server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    @Override
    public void onDisable() {
        System.out.println("[NPCPlugin] onDisable chiamato!");
        /*
        try {
            // if (server != null) server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
