package it.gael.npc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.gael.npc.wrapper.MockHytaleAPI; // Fallback or wrapper interface

// Using reflection or wrapper until exact packages are known
// To avoid compilation errors with unknown packages

public class ActionHandler {
    private static final Logger logger = LoggerFactory.getLogger("NPC-Brain");

    public void handle(String npcName, String command, String targetName, String chatContent) {
        logger.info("Executing Action -> Command: [{}], Target: [{}], Chat: [{}]", command, targetName, chatContent);

        if (command == null) {
            logger.warn("Received null command, ignoring.");
            return;
        }

        // Placeholder for Entity retrieval
        // Object npcEntity = MockHytaleAPI.findEntity(npcName);
        
        // if (npcEntity == null) {
        //    logger.error("NPC '{}' not found (Mock/Real API not connected)!", npcName);
        //    return;
        // }

        switch (command.toUpperCase()) {
            case "ATTACK":
                handleAttack(npcName, targetName);
                break;
            case "FOLLOW":
                handleFollow(npcName, targetName);
                break;
            case "MINE":
                handleMine(npcName, targetName);
                break;
            case "CHAT":
                handleChat(npcName, chatContent);
                break;
            case "IDLE":
                handleIdle(npcName);
                break;
            default:
                logger.warn("Unknown command received from Brain: {}", command);
        }
    }

    private void handleAttack(String npcName, String targetName) {
        if (targetName == null || targetName.isEmpty()) return;
        logger.info("⚔️ [STUB] NPC {} set to Combat mode against {}", npcName, targetName);
        // Implementation waiting for correct package names
    }

    private void handleFollow(String npcName, String targetName) {
        if (targetName == null || targetName.isEmpty()) return;
        logger.info("🏃 [STUB] NPC {} following {}", npcName, targetName);
    }

    private void handleMine(String npcName, String blockName) {
        logger.info("⛏️ [STUB] NPC {} moving to mine {}", npcName, blockName);
    }

    private void handleChat(String npcName, String message) {
        if (message == null || message.isEmpty()) return;
        logger.info("💬 [STUB] NPC {} says: {}", npcName, message);
    }

    private void handleIdle(String npcName) {
        logger.info("💤 [STUB] NPC {} idling", npcName);
    }
}
