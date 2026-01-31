package it.gael.npc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// CORRECT Hytale Server Imports based on jar_structure.txt
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.UUID;

public class ActionHandler {
    private static final Logger logger = LoggerFactory.getLogger("NPC-Brain");
    
    // Static reference to world - in a real mod this should be injected or retrieved via a Manager
    public static World globalWorld; 

    public void handle(String npcName, String command, String targetName, String chatContent) {
        logger.info("Executing Action -> Command: [{}], Target: [{}], Chat: [{}]", command, targetName, chatContent);

        if (command == null) {
            logger.warn("Received null command, ignoring.");
            return;
        }

        if (globalWorld == null) {
             logger.warn("Global World reference is null! Cannot execute commands.");
             return;
        }

        Entity npcEntity = findEntityByName(npcName);
        
        if (npcEntity == null) {
            logger.error("NPC '{}' not found in the world!", npcName);
            return;
        }

        switch (command.toUpperCase()) {
            case "ATTACK":
                handleAttack(npcEntity, targetName);
                break;
            case "FOLLOW":
                handleFollow(npcEntity, targetName);
                break;
            case "MINE":
                handleMine(npcEntity, targetName);
                break;
            case "CHAT":
                handleChat(npcEntity, chatContent);
                break;
            case "IDLE":
                handleIdle(npcEntity);
                break;
            default:
                logger.warn("Unknown command received from Brain: {}", command);
        }
    }

    private void handleAttack(Entity npc, String targetName) {
        if (targetName == null || targetName.isEmpty()) return;

        Entity target = findEntityByName(targetName);
        if (target == null) {
            logger.warn("Target '{}' not found for attack.", targetName);
            return;
        }

        setNPCMemory(npc, "LockedTarget", target);
        setNPCState(npc, "Combat");
        
        logger.info("⚔️ NPC {} set to Combat mode against {}", getEntityName(npc), targetName);
    }

    private void handleFollow(Entity npc, String targetName) {
        if (targetName == null || targetName.isEmpty()) return;

        Entity target = findEntityByName(targetName);
        if (target == null) {
            logger.warn("Target '{}' not found for following.", targetName);
            return;
        }

        setNPCMemory(npc, "LockedTarget", target);
        setNPCState(npc, "Alerted");
        
        logger.info("🏃 NPC {} following {}", getEntityName(npc), targetName);
    }

    private void handleMine(Entity npc, String blockName) {
        logger.info("⛏️ [Stub] Mining logic for {}", blockName);
    }

    private void handleChat(Entity npc, String message) {
        if (message == null || message.isEmpty()) return;
        logger.info("[Chat] {}: {}", getEntityName(npc), message);
    }

    private void handleIdle(Entity npc) {
        setNPCState(npc, "Idle");
        setNPCMemory(npc, "LockedTarget", null);
    }

    // --- Helper Methods ---

    private Entity findEntityByName(String name) {
        if (globalWorld == null) return null;

        try {
            // Using reflection for getEntities to be safe
            Method getEntitiesMethod = globalWorld.getClass().getMethod("getEntities");
            Collection<Entity> entities = (Collection<Entity>) getEntitiesMethod.invoke(globalWorld);
            
            for (Entity e : entities) {
                String eName = getEntityName(e);
                if (name.equals(eName) || (e instanceof Player && name.equals(getEntityName(e)))) {
                    return e;
                }
            }
        } catch (Exception e) {
            logger.error("Error finding entity: " + e.getMessage());
        }
        return null;
    }

    private String getEntityName(Entity e) {
        try {
            // Try standard getters via reflection to avoid "cannot find symbol"
            // Ordered by likelihood in Hytale API
            String[] methods = {"getName", "getDisplayName", "getCustomName", "toString"};
            
            for (String methodName : methods) {
                try {
                    Method m = e.getClass().getMethod(methodName);
                    Object result = m.invoke(e);
                    if (result != null) return result.toString();
                } catch (NoSuchMethodException ignored) {
                    // Try next method
                }
            }
        } catch (Exception ex) {
            // Ignore
        }
        // Fallback
        return e.toString();
    }

    // --- Reflection Helpers for Components ---

    private void setNPCState(Entity entity, String state) {
        try {
            logger.info(">> [Simulated] Setting State to: " + state);
            // Future: Implement Component lookup via reflection using:
            // com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent (example path)
        } catch (Exception e) {
            logger.error("Failed to set NPC state: " + e.getMessage());
        }
    }

    private void setNPCMemory(Entity entity, String key, Object value) {
        try {
            logger.info(">> [Simulated] Setting Memory '{}' to {}", key, value);
        } catch (Exception e) {
             logger.error("Failed to set NPC memory: " + e.getMessage());
        }
    }
}
