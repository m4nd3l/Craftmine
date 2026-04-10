package dev.m4nd3l.craftmine.blocks;

import dev.m4nd3l.craftmine.blocks.settings.Color;

public class BlockSettings {
    public static BlockSettings NONE = new BlockSettings();

    private Color color;

    public BlockSettings color(Color color) { this.color = color; return this; }

    public Color getColor() { return color; }

}
