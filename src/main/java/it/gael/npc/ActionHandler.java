package it.gael.npc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// CORRECT Hytale Server Imports based on jar_structure.txt
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;

// We use reflection for Components to avoid missing class errors if names differ slightly
// but we found these in the structure:
// com.hypixel.hytale.component.Component
// com.hypixel.hytale.server.npc.core.components.entity.ActionSetStat (Example of NPC component)

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

        // Logic derived from PDF: Set "LockedTarget" in memory/blackboard
        setNPCMemory(npc, "LockedTarget", target);
        
        // Force state change to "Combat"
        setNPCState(npc, "Combat");
        
        logger.info("⚔️ NPC {} set to Combat mode against {}", npc.toString(), targetName);
    }

    private void handleFollow(Entity npc, String targetName) {
        if (targetName == null || targetName.isEmpty()) return;

        Entity target = findEntityByName(targetName);
        if (target == null) {
            logger.warn("Target '{}' not found for following.", targetName);
            return;
        }

        // Use "Alerted" or "Chase" state as per PDF
        setNPCMemory(npc, "LockedTarget", target);
        setNPCState(npc, "Alerted");
        
        logger.info("🏃 NPC {} following {}", npc.toString(), targetName);
    }

    private void handleMine(Entity npc, String blockName) {
        logger.info("⛏️ [Stub] Mining logic for {}", blockName);
        // Mining would require setting a path to a block, which involves Navigation components
    }

    private void handleChat(Entity npc, String message) {
        if (message == null || message.isEmpty()) return;
        
        // In Hytale we might want to spawn a chat bubble or send a message
        // For now, logging it as a broadcast
        logger.info("[Chat] {}: {}", npc.toString(), message);
    }

    private void handleIdle(Entity npc) {
        setNPCState(npc, "Idle");
        setNPCMemory(npc, "LockedTarget", null);
    }

    // --- Helper Methods ---

    private Entity findEntityByName(String name) {
        if (globalWorld == null) return null;

        // Note: globalWorld.getEntities() is an assumption on the method name based on standard APIs
        // If exact method differs, reflection might be needed.
        // Based on structure: com.hypixel.hytale.server.core.universe.world.World exists.
        
        try {
            // Using reflection for getEntities to be safe
            Method getEntitiesMethod = globalWorld.getClass().getMethod("getEntities");
            Collection<Entity> entities = (Collection<Entity>) getEntitiesMethod.invoke(globalWorld);
            
            for (Entity e : entities) {
                // Assuming Entity has getName() or toString() useful
                // Structure shows com.hypixel.hytale.server.core.entity.Entity
                if (name.equals(e.toString()) || (e instanceof Player && ((Player)e).getName().equals(name))) {
                    return e;
                }
            }
        } catch (Exception e) {
            logger.error("Error finding entity: " + e.getMessage());
        }
        return null;
    }

    // --- Reflection Helpers for Components ---
    // We use reflection here because we don't want to import Component classes that might change names
    // or be in unexpected sub-packages (like com.hypixel.hytale.server.npc.core.components...)

    private void setNPCState(Entity entity, String state) {
        try {
            // Looking for StateMachineComponent or similar
            // From structure: com.hypixel.hytale.server.components.ai.StateMachineComponent was MISSING
            // But we saw com.hypixel.hytale.server.npc.core.components...
            
            // For now, we print a log because we need the exact Component class name for State Machine
            // from the jar structure to implement this via reflection accurately.
            // The PDF calls it "Component_Instruction_..." inside JSON, 
            // but in Java it's likely a "StateMachineComponent" or "AIComponent".
            
            logger.info(">> [Simulated] Setting State to: " + state);
            
            // Pseudo-code for reflection:
            // Component fsm = entity.getComponent(Class.forName("com.hypixel.hytale...StateMachineComponent"));
            // fsm.setState(state);
            
        } catch (Exception e) {
            logger.error("Failed to set NPC state: " + e.getMessage());
        }
    }

    private void setNPCMemory(Entity entity, String key, Object value) {
        try {
            logger.info(">> [Simulated] Setting Memory '{}' to {}", key, value);
             // Pseudo-code:
            // Component mem = entity.getComponent(Class.forName("...MemoryComponent"));
            // mem.set(key, value);
        } catch (Exception e) {
             logger.error("Failed to set NPC memory: " + e.getMessage());
        }
    }
}
