package dev.m4nd3l.craftmine.json;

import com.google.gson.annotations.SerializedName;

public class SettingsData {
    @SerializedName("player_name")
    private String playerName = "Player";

    @SerializedName("render_distance")
    private int renderDistance = 12;

    @SerializedName("hitboxes_lines_width")
    private float hitboxesLinesWidth = 2f;

    public String getPlayerName() { return playerName; }
    public SettingsData setPlayerName(String playerName) { this.playerName = playerName; return this; }

    public int getRenderDistance() { return renderDistance; }
    public SettingsData setRenderDistance(int renderDistance) { this.renderDistance = renderDistance; return this; }

    public float getHitboxesLinesWidth() { return hitboxesLinesWidth; }
    public SettingsData setHitboxesLinesWidth(float hitboxesLinesWidth) { this.hitboxesLinesWidth = hitboxesLinesWidth; return this; }
}
