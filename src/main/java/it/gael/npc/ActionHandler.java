package it.gael.npc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionHandler {
    private static final Logger logger = LoggerFactory.getLogger("NPC-Brain");

    public void handle(String npcName, String command, String targetName, String chatContent) {
        logger.info("Executing Action -> Command: [{}], Target: [{}], Chat: [{}]", command, targetName, chatContent);

        if (command == null) {
            logger.warn("Received null command, ignoring.");
            return;
        }

        switch (command.toUpperCase()) {
            case "ATTACK":
                handleAttack(targetName);
                break;
            case "FOLLOW":
                handleFollow(targetName);
                break;
            case "MINE":
                handleMine(targetName);
                break;
            case "CHAT":
                handleChat(chatContent);
                break;
            case "IDLE":
                logger.debug("Brain decided to IDLE.");
                break;
            default:
                logger.warn("Unknown command received from Brain: {}", command);
        }
    }

    private void handleAttack(String targetName) {
        if (targetName == null || targetName.isEmpty()) {
             logger.warn("Cannot attack: Target name is missing.");
             return;
        }
        // TODO: Sanasol API Implementation
        // Entity target = HytaleServer.getEntityByName(targetName);
        // if (target != null) NPC.getNavigator().setTarget(target);
        logger.info("⚔️ NPC is attacking {}", targetName);
    }

    private void handleFollow(String targetName) {
        if (targetName == null || targetName.isEmpty()) {
            logger.warn("Cannot follow: Target name is missing.");
            return;
        }
        // TODO: Sanasol API Implementation
        // Player player = HytaleServer.getPlayer(targetName);
        // if (player != null) NPC.getNavigator().follow(player);
        logger.info("🏃 NPC is following {}", targetName);
    }

    private void handleMine(String blockName) {
         if (blockName == null || blockName.isEmpty()) {
            logger.warn("Cannot mine: Block name is missing.");
            return;
        }
        // TODO: Sanasol API Implementation
        // Block block = findNearestBlock(blockName);
        // if (block != null) NPC.mine(block);
        logger.info("⛏️ NPC is mining {}", blockName);
    }

    private void handleChat(String message) {
        if (message == null || message.isEmpty()) return;
        
        // TODO: Sanasol API Implementation
        // HytaleServer.broadcastMessage("[Gillian] " + message);
        logger.info("💬 NPC says: {}", message);
    }
}
