package dev.m4nd3l.craftmine.json;

import com.google.gson.annotations.SerializedName;
import dev.m4nd3l.craftmine.renderer.Camera;

public class WorldData {
    @SerializedName("player")
    private /*Player player*/ Camera player;

    @SerializedName("world_name")
    private String worldName;

    public Camera getPlayer() { return player; }
    public WorldData setPlayer(Camera player) { this.player = player; return this; }

    public String getWorldName() { return worldName; }
    public WorldData setWorldName(String worldName) { this.worldName = worldName; return this; }
}