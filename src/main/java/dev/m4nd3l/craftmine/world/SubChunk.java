package dev.m4nd3l.craftmine.world;

import com.google.gson.annotations.SerializedName;
import dev.m4nd3l.craftmine.coordinates.LocalSubChunkCoordinates;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.world.SubChunkRenderer;
import dev.m4nd3l.craftmine.world.communication.Communication;
import dev.m4nd3l.craftmine.world.communication.TellWorld;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

public class SubChunk {
    @SerializedName("block_ids")
    private short[] blocks;

    @SerializedName("renderer")
    private SubChunkRenderer renderer;

    @SerializedName("coordinates")
    private SubChunkCoordinates coordinates;

    public SubChunk(SubChunkCoordinates coordinates) {
        blocks = new short[Consts.SIZE * Consts.SIZE * Consts.SIZE];
        renderer = new SubChunkRenderer().initialize();
        this.coordinates = coordinates;
        renderer.dirty();
    }

    public SubChunk() {}

    public void postLoadInit() {
        if (renderer == null) renderer = new SubChunkRenderer();
        renderer = renderer.initialize();
        TellWorld.tellWorld(coordinates, Communication.REMESH_REQUEST);
    }

    public void update(float delta) {
        if (renderer.isDirty()) {
            TellWorld.tellWorld(coordinates, Communication.REMESH_REQUEST);
            renderer.undirty();
        }
    }
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

    public void newMesh(FloatArrayList vertices) { renderer.setVertices(vertices); }
    public void uploadToGPU() { renderer.uploadToGPU(); }

    private short getBlockID(int x, int y, int z) { return blocks[x + Consts.SIZE * (y + Consts.SIZE * z)]; }
    private BlockRegistry getBlock(int x, int y, int z) { return BlockRegistries.getBlock(getBlockID(x, y, z)); }
    private int getIndex(int x, int y, int z) { return x + Consts.SIZE * (y + Consts.SIZE * z); }
    private int getIndex(LocalSubChunkCoordinates coordinates) { return getIndex(coordinates.getX(), coordinates.getY(), coordinates.getZ()); }

    public SubChunkCoordinates getCoordinates() { return coordinates; }
    public short[] getBlocks() { return blocks; }
}