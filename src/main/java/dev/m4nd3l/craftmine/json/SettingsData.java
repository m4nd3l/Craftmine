package dev.m4nd3l.craftmine.json;

import com.google.gson.annotations.SerializedName;

public class SettingsData {
    @SerializedName("player_name")
    private String playerName;

    @SerializedName("render_distance")
    private int renderDistance;

    public String getPlayerName() { return playerName; }
    public SettingsData setPlayerName(String playerName) { this.playerName = playerName; return this; }

    public int getRenderDistance() { return renderDistance; }
    public SettingsData setRenderDistance(int renderDistance) { this.renderDistance = renderDistance; return this; }
}
