package dev.m4nd3l.craftmine.world;

import com.google.gson.annotations.SerializedName;
import dev.m4nd3l.craftmine.coordinates.LocalSubChunkCoordinates;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.SubChunkRenderer;

public class SubChunk {
    private transient int SIZE = 16;

    @SerializedName("block_ids")
    private short[] blocks;

    @SerializedName("renderer")
    private SubChunkRenderer renderer;

    @SerializedName("coordinates")
    private SubChunkCoordinates coordinates;

    public SubChunk(SubChunkCoordinates coordinates) {
        blocks = new short[SIZE * SIZE * SIZE];
        renderer = new SubChunkRenderer().initialize(SIZE, coordinates);
        this.coordinates = coordinates;
        renderer.dirty();
    }

    public SubChunk() {}

    public void postLoadInit() {
        if (renderer == null) renderer = new SubChunkRenderer();
        renderer = renderer.initialize(SIZE, coordinates);
    }

    public void update(float delta) { if (renderer.isDirty()) renderer.generateMesh(blocks); }
    public void render() { renderer.render(); }
    public void delete() { renderer.delete(); }

    public void placeBlock(LocalSubChunkCoordinates coordinates, BlockRegistry block) {
        renderer.dirty();
        blocks[getIndex(coordinates)] = (short) block.getId();
    }

    public void digBlock(LocalSubChunkCoordinates coordinates) {
        renderer.dirty();
        blocks[getIndex(coordinates)] = 0;
    }

    private short getBlockID(int x, int y, int z) { return blocks[x + SIZE * (y + SIZE * z)]; }
    private BlockRegistry getBlock(int x, int y, int z) { return BlockRegistries.getBlock(getBlockID(x, y, z)); }
    private int getIndex(int x, int y, int z) { return x + SIZE * (y + SIZE * z); }
    private int getIndex(LocalSubChunkCoordinates coordinates) { return getIndex(coordinates.getX(), coordinates.getY(), coordinates.getZ()); }

    public SubChunkCoordinates getCoordinates() { return coordinates; }
}