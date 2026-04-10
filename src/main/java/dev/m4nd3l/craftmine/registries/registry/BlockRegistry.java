package dev.m4nd3l.craftmine.registries.registry;

import dev.m4nd3l.craftmine.blocks.Block;

public class BlockRegistry extends Registry<Block> {
    private int topTextureID, bottomTextureID, sideTextureID;

    public BlockRegistry(int id, int bottomTextureID, int sideTextureID, int topTextureID, Block instance) {
        super(id, instance);
        this.bottomTextureID = bottomTextureID;
        this.sideTextureID = sideTextureID;
        this.topTextureID = topTextureID;
    }

    public BlockRegistry(int id, int totalTextureID, Block instance) {
        this(id, totalTextureID, totalTextureID, totalTextureID, instance);
    }

    public int getBottomTextureID() { return bottomTextureID; }
    public int getSideTextureID() { return sideTextureID; }
    public int getTopTextureID() { return topTextureID; }
}
