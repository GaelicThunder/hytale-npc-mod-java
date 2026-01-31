package it.gael.npc;

import com.hypixel.hytale.server.core.universe.world.World;
import it.gael.npc.network.ZMQServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

public class NPCPlugin {
    private static final Logger logger = LoggerFactory.getLogger("NPC-Plugin");
    private ZMQServer server;
    private ActionHandler actionHandler;

    public void onServerStart() {
        logger.info("Initializing NPC Brain Connector...");

        // Try to inject world via Reflection on common entry points since HytaleServer class is missing/obfuscated
        try {
            // Attempt 1: Look for a static getWorld in com.hypixel.hytale.Main (hypothetical)
            Class<?> mainClass = Class.forName("com.hypixel.hytale.Main");
            Method getWorldMethod = mainClass.getMethod("getWorld");
            ActionHandler.globalWorld = (World) getWorldMethod.invoke(null);
            logger.info("World injected via Main.getWorld()");
        } catch (Exception e) {
            logger.warn("Could not inject World via reflection (ActionHandler will check for world later/null-safe): " + e.getMessage());
        }

        actionHandler = new ActionHandler();
        
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
