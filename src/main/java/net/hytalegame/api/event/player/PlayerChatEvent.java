package net.hytalegame.api.event.player;
import net.hytalegame.api.entity.Player;
// MOCK CLASS
public class PlayerChatEvent {
    public Player getPlayer() { return new Player("MockUser"); }
    public String getMessage() { return "Hello"; }
}
