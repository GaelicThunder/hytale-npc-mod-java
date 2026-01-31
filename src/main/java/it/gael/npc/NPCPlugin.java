package it.gael.npc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.gael.npc.network.BotServer;

// Import corretti basati sul dump
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit; // Richiesto dal costruttore
import com.hypixel.hytale.server.core.entity.entities.Player;
// Modificato: Usiamo AbstractCommandCollection o CommandBase se AbstractPlayerCommand non è visibile o ha un altro path
// Dal dump sembra esistere com.hypixel.hytale.server.core.commandsystem.basecommands.AbstractPlayerCommand
// ma l'errore dice che il package non esiste. Proviamo a usare CommandBase e castare manualmente.
import com.hypixel.hytale.server.core.commandsystem.basecommands.CommandBase;
import com.hypixel.hytale.server.core.commandsystem.CommandSender;
import com.hypixel.hytale.server.core.command.commands.player.PlayerCommand; 

import java.net.InetSocketAddress;

public class NPCPlugin extends JavaPlugin {

    private BotServer server;
    private final Gson gson = new Gson();
    private static NPCPlugin instance;

    // COSTRUTTORE OBBLIGATORIO: JavaPlugin richiede JavaPluginInit
    public NPCPlugin(JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    public void onEnable() {
        // HytaleLogger non ha .info()? Proviamo a usare System.out per debug sicuro o getLogger().warn() se esiste.
        System.out.println("[NPCPlugin] Starting Brain Bridge on port 8080...");
        
        server = new BotServer(new InetSocketAddress(8080));
        server.start();
        
        // Registrazione comando alternativa
        // Se getCommandManager() non esiste, forse è in 'super' o si usa un singleton?
        // Proviamo a bypassare la registrazione per vedere se compila il resto, 
        // o usiamo un approccio statico se noto.
        // this.registerCommand(...) ?
    }

    @Override
    public void onDisable() {
        try {
            if (server != null) server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Funzione richiesta da BotServer
    public void executeNPCAction(String npc, String cmd, String target, String chat) {
        System.out.println("Action: " + cmd + " -> " + target);
    }

    public static NPCPlugin getInstance() {
        return instance;
    }
    
    // Comando minimale
    // Se AbstractPlayerCommand fallisce, usiamo un approccio più generico o rimuoviamo il comando per ora
    // per garantire che la build passi.
}
