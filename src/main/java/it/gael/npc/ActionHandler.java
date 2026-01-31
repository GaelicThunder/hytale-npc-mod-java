package it.gael.npc;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Inferred Hytale Server Imports (Standard API)
import com.hypixel.hytale.server.HytaleServer;
import com.hypixel.hytale.server.entity.Entity;
import com.hypixel.hytale.server.entity.Player;
import com.hypixel.hytale.server.entity.npc.NPC;
import com.hypixel.hytale.server.world.World;
import com.hypixel.hytale.server.components.ai.StateMachineComponent;
import com.hypixel.hytale.server.components.ai.MemoryComponent;

import java.util.UUID;

public class ActionHandler {
    private static final Logger logger = LoggerFactory.getLogger("NPC-Brain");

    public void handle(String npcName, String command, String targetName, String chatContent) {
        logger.info("Executing Action -> Command: [{}], Target: [{}], Chat: [{}]", command, targetName, chatContent);

        if (command == null) {
            logger.warn("Received null command, ignoring.");
            return;
        }

        // Recuperiamo l'entità NPC (Gillian)
        // Assumiamo che ci sia un metodo per trovare entità per nome o tag
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

        // 1. Impostiamo il target nella memoria dell'NPC (Blackboard)
        // Come da PDF, usiamo "LockedTarget" come TargetSlot
        setNPCMemory(npc, "LockedTarget", target);

        // 2. Forziamo il cambio di stato a "Combat" o "Alerted"
        // Questo attiverà i behavior tree definiti nel JSON (Component_Instruction_Intelligent_Chase)
        setNPCState(npc, "Combat");
        
        logger.info("⚔️ NPC set to Combat mode against {}", targetName);
    }

    private void handleFollow(Entity npc, String targetName) {
        if (targetName == null || targetName.isEmpty()) return;

        Entity target = findEntityByName(targetName);
        if (target == null) {
            logger.warn("Target '{}' not found for following.", targetName);
            return;
        }

        // Per seguire, usiamo la logica di Chase ma senza l'intento aggressivo immediato,
        // oppure un Custom State "Follow" se definito nel template JSON.
        // Se non esiste, usiamo "Alerted" che segue il target.
        setNPCMemory(npc, "LockedTarget", target);
        setNPCState(npc, "Alerted"); // O "Chase" se esposto direttamente
        
        logger.info("🏃 NPC following {}", targetName);
    }

    private void handleMine(Entity npc, String blockName) {
        // Il mining non è esplicito nel PDF, ma possiamo simulare l'intento
        // muovendo l'NPC verso il blocco e riproducendo l'animazione.
        logger.info("⛏️ Mining logic not fully mapped to JSON template yet. Sending Move command.");
        
        // TODO: Trovare coordinate del blocco più vicino di tipo 'blockName'
        // Vector3f loc = findNearestBlock(npc.getLocation(), blockName);
        // npc.getNavigator().setDestination(loc);
    }

    private void handleChat(Entity npc, String message) {
        if (message == null || message.isEmpty()) return;
        
        // Hytale ha le chat bubbles sopra la testa
        HytaleServer.getWorld().broadcastMessage("[Gillian] " + message);
        
        // Possiamo anche far riprodurre un'animazione "Talk"
        // playAnimation(npc, "Talk");
    }

    private void handleIdle(Entity npc) {
        setNPCState(npc, "Idle");
        setNPCMemory(npc, "LockedTarget", null); // Clear target
    }

    // --- Helper Methods (Abstraction over Hytale API) ---

    private Entity findEntityByName(String name) {
        // Cerca tra i player
        Player player = HytaleServer.getPlayer(name);
        if (player != null) return player;

        // Cerca tra le entità caricate (API Ipotetica standard)
        for (Entity e : HytaleServer.getWorld().getEntities()) {
            if (name.equals(e.getName()) || name.equals(e.getCustomName())) {
                return e;
            }
        }
        return null;
    }

    private void setNPCState(Entity entity, String state) {
        // Accesso al componente StateMachine (ECS)
        StateMachineComponent fsm = entity.getComponent(StateMachineComponent.class);
        if (fsm != null) {
            fsm.setState(state);
        } else {
            logger.warn("Entity {} has no StateMachineComponent!", entity.getName());
        }
    }

    private void setNPCMemory(Entity entity, String key, Object value) {
        // Accesso alla memoria dell'AI (Blackboard)
        MemoryComponent mem = entity.getComponent(MemoryComponent.class);
        if (mem != null) {
            mem.set(key, value);
        }
    }
}
