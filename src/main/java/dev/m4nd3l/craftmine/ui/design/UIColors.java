package dev.m4nd3l.craftmine.ui.design;

public enum UIColors {
    // --- REDS ---
    RED_LIGHT(1.0f, 0.4f, 0.4f, 1.0f),
    RED(1.0f, 0.0f, 0.0f, 1.0f),
    RED_DARK(0.5f, 0.0f, 0.0f, 1.0f),

    // --- GREENS ---
    GREEN_LIGHT(0.4f, 1.0f, 0.4f, 1.0f),
    GREEN(0.0f, 1.0f, 0.0f, 1.0f),
    GREEN_DARK(0.0f, 0.5f, 0.0f, 1.0f),

    // --- BLUES ---
    BLUE_LIGHT(0.4f, 0.4f, 1.0f, 1.0f),
    BLUE(0.0f, 0.0f, 1.0f, 1.0f),
    BLUE_DARK(0.0f, 0.0f, 0.5f, 1.0f),

    // --- YELLOWS ---
    YELLOW_LIGHT(1.0f, 1.0f, 0.5f, 1.0f),
    YELLOW(1.0f, 1.0f, 0.0f, 1.0f),
    YELLOW_DARK(0.6f, 0.6f, 0.0f, 1.0f),

    // --- PURPLES ---
    PURPLE_LIGHT(0.8f, 0.4f, 1.0f, 1.0f),
    PURPLE(0.6f, 0.0f, 1.0f, 1.0f),
    PURPLE_DARK(0.3f, 0.0f, 0.5f, 1.0f),

    // --- ORANGES ---
    ORANGE_LIGHT(1.0f, 0.7f, 0.3f, 1.0f),
    ORANGE(1.0f, 0.5f, 0.0f, 1.0f),
    ORANGE_DARK(0.6f, 0.3f, 0.0f, 1.0f),

    // --- CYANS ---
    CYAN_LIGHT(0.5f, 1.0f, 1.0f, 1.0f),
    CYAN(0.0f, 1.0f, 1.0f, 1.0f),
    CYAN_DARK(0.0f, 0.5f, 0.5f, 1.0f),

    // --- GREYSCALE (Building Blocks of UI) ---
    WHITE(1.0f, 1.0f, 1.0f, 1.0f),
    GRAY_LIGHT(0.7f, 0.7f, 0.7f, 1.0f),
    GRAY(0.5f, 0.5f, 0.5f, 1.0f),
    GRAY_DARK(0.2f, 0.2f, 0.2f, 1.0f),
    BLACK(0.0f, 0.0f, 0.0f, 1.0f),

    // --- SPECIALS ---
    TRANSPARENT(0.0f, 0.0f, 0.0f, 0.0f),
    OVERLAY_SHADOW(0.0f, 0.0f, 0.0f, 0.5f);

    private UIColor color;
    UIColors(float r, float g, float b, float a) { color = new UIColor(r, g, b, a); }
    public UIColor getColor() { return color; }
}
