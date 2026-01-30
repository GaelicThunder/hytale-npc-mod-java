package net.hytalegame.api.entity;

// MOCK CLASS
public class Player extends Entity {
    private String name;
    public Player(String name) { this.name = name; }
    public String getName() { return name; }
    public float getHealth() { return 100.0f; }
    public Object getPosition() { return "0,0,0"; }
    public World getWorld() { return new World(); }
}
