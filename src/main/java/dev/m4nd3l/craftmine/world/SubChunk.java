package dev.m4nd3l.craftmine.world;

import com.google.gson.annotations.SerializedName;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.Camera;
import dev.m4nd3l.craftmine.renderer.renderers.SubChunkRenderer;
import dev.m4nd3l.craftmine.world.communication.Communication;
import dev.m4nd3l.craftmine.world.communication.WorldCommunication;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

public class SubChunk {
    @SerializedName("block_ids")
    private short[] blocks;

    private transient SubChunkRenderer renderer;

    @SerializedName("coordinates")
    private SubChunkCoordinates coordinates;

    private transient boolean isSeen = true;

    public SubChunk(SubChunkCoordinates coordinates) {
        blocks = new short[Consts.SIZE * Consts.SIZE * Consts.SIZE];
        this.coordinates = coordinates;
    }

    public SubChunk() {}

    public void postLoadInit() {
        if (renderer == null) renderer = new SubChunkRenderer();
        renderer = renderer.initialize();
        renderer.dirty();
        WorldCommunication.tellWorld(coordinates, Communication.REMESH_REQUEST);
    }

    public void update(float delta) {
        if (renderer == null) WorldCommunication.tellWorld(coordinates, Communication.INITIALIZE_SUBCHUNK);
        if (renderer != null && renderer.isDirty() && isSeen) {
            WorldCommunication.tellWorld(coordinates, Communication.REMESH_REQUEST);
            renderer.undirty();
        }
    }
    public void render(Camera camera) {
        if (renderer == null) WorldCommunication.tellWorld(coordinates, Communication.INITIALIZE_SUBCHUNK);
        if (isSeen(camera) && renderer != null) renderer.render();
    }
    public void delete() { renderer.delete(); }

    public void placeBlock(int x, int y, int z, BlockRegistry block) { placeBlock(x, y, z, block, true); }

    public void placeBlock(int x, int y, int z, BlockRegistry block, boolean setDirty) {
        if (setDirty) renderer.dirty();
        blocks[getIndex(x, y, z)] = (short) block.getId();
    }

    public void setDirty() { renderer.dirty(); }

    public void digBlock(int x, int y, int z) {
        renderer.dirty();
        blocks[getIndex(x, y, z)] = 0;
    }

    public void newMesh(FloatArrayList vertices) { renderer.setVertices(vertices); }
    public void uploadToGPU() {
        if (renderer == null) WorldCommunication.tellWorld(coordinates, Communication.INITIALIZE_SUBCHUNK);
        if (renderer != null) renderer.uploadToGPU();
    }

    private short getBlockID(int x, int y, int z) { return blocks[x + Consts.SIZE * (y + Consts.SIZE * z)]; }
    public BlockRegistry getBlock(int x, int y, int z) { return BlockRegistries.getBlock(getBlockID(x, y, z)); }
    private int getIndex(int x, int y, int z) { return x + Consts.SIZE * (y + Consts.SIZE * z); }

    public SubChunkCoordinates getCoordinates() { return coordinates; }
    public short[] getBlocks() { return blocks; }

    private boolean isSeen(Camera camera) {
        float margin = 0.5f;
        float minX = (coordinates.getX() * Consts.SIZE) - margin;
        float minY = (coordinates.getY() * Consts.SIZE) - margin;
        float minZ = (coordinates.getZ() * Consts.SIZE) - margin;
        float maxX = minX + Consts.SIZE + (margin * 2);
        float maxY = minY + Consts.SIZE + (margin * 2);
        float maxZ = minZ + Consts.SIZE + (margin * 2);

        isSeen = camera.isInsideFrustum(minX, minY, minZ, maxX, maxY, maxZ);
        return isSeen;
    }
}