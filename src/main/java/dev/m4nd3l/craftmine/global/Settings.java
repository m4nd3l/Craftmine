package dev.m4nd3l.craftmine.global;

import dev.m4nd3l.craftmine.json.SettingsData;
import dev.m4nd3l.craftmine.renderer.util.MFile;

public class Settings {
    public static SettingsData settings;

    static {
        MFile settingsFile = new MFile("data", "settings.json");
        if (!settingsFile.exists()) settingsFile.create("{\"player_name\":\"Player\",\"render_distance\":10}");
        settings = Consts.gson.fromJson(settingsFile.readString(), SettingsData.class);
    }
}
