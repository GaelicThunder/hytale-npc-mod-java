package it.gael.npc;

import com.hypixel.hytale.server.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.World;
import it.gael.npc.network.ZMQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NPCPlugin {
    private static final Logger logger = LoggerFactory.getLogger("NPC-Plugin");
    private ZMQServer server;
    private ActionHandler actionHandler;

    // Entry point standard per Hytale mods (spesso 'onServerStart' o costruttore)
    // Assumiamo che Hytale usi ServiceLoader o un metodo main/init riflesso
    public void onServerStart() {
        logger.info("Initializing NPC Brain Connector...");

        // Inject World Reference
        // In una mod reale, questo avviene tramite Eventi (ServerStartedEvent)
        // Qui lo facciamo staticamente come Proof of Concept
        try {
            ActionHandler.globalWorld = HytaleServer.getWorld();
        } catch (Exception e) {
            logger.warn("Could not inject World immediately (Server might be starting up): " + e.getMessage());
        }

        actionHandler = new ActionHandler();
        
        // Avviamo il server ZMQ sulla porta 5555
        // Il Brain Python si collegherà a tcp://localhost:5555
        server = new ZMQServer(actionHandler);
        server.start();
        
        logger.info("NPC Brain Connector Ready!");
    }

    public void onServerStop() {
        if (server != null) {
            server.stop();
            logger.info("ZMQ Server stopped.");
        }
    }
}
