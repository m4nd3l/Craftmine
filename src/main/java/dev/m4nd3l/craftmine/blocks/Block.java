package dev.m4nd3l.craftmine.blocks;

public class Block {
    private String nameID;
    private BlockSettings settings;

    public Block(String nameID, BlockSettings settings) { this.nameID = nameID; this.settings = settings; }

    public String getNameID() { return nameID; }
    public BlockSettings getSettings() { return settings; }
}
