package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.Camera;

import java.util.ArrayList;
import java.util.List;

public class Chunk {
    private List<SubChunk> subChunks;
    private ChunkCoordinates coordinates;

    public Chunk(ChunkCoordinates coordinates) {
        subChunks = new ArrayList<>();
        this.coordinates = coordinates;
    }

    public Chunk() {}

    public void loadAfterInit() { subChunks.forEach(SubChunk::postLoadInit); }

    public void upload(SubChunkCoordinates coordinates) {
        SubChunk subChunk = getSubChunk(coordinates);
        if (subChunk == null) return;
        subChunk.uploadToGPU();
    }

    // region INTERACTION
    public void placeBlock(int x, int y, int z, BlockRegistry block) {
        getSubChunk(x, y, z).placeBlock(x & 15, y & 15, z & 15, block);
    }

    public void digBlock(int x, int y, int z) {
        getSubChunk(x, y, z).digBlock(x & 15, y & 15, z & 15);
    }
    // endregion
    // region MAIN METHODS
    public void update(float delta) { subChunks.forEach(subChunk -> subChunk.update(delta)); }
    public void render(Camera camera) { subChunks.forEach(subChunk -> subChunk.render(camera)); }
    public void delete() { subChunks.forEach(SubChunk::delete); }
    // endregion
    // region HELPERS
    public ChunkCoordinates getCoordinates() { return coordinates; }
    public SubChunk getSubChunk(Coordinates coordinates) {
        SubChunkCoordinates subChunkCoordinates = CoordinatesConverter.toSubChunk(coordinates);
        if (subChunks.stream().anyMatch(subChunk -> subChunk.getCoordinates().equals(subChunkCoordinates)))
            return subChunks.stream()
                    .filter(subChunk -> subChunk.getCoordinates().equals(subChunkCoordinates))
                    .findFirst()
                    .orElse(null);
        SubChunk newChunk = new SubChunk(subChunkCoordinates);
        subChunks.add(newChunk);
        return newChunk;
    }

    public SubChunk getSubChunk(int x, int y, int z) {
        SubChunkCoordinates subCoords = CoordinatesConverter.toSubChunk(new BlockCoordinates(x, y, z));
        for (SubChunk sc : subChunks) if (sc.getCoordinates().equals(subCoords)) return sc;
        SubChunk newSc = new SubChunk(subCoords);
        subChunks.add(newSc);
        return newSc;
    }

    public BlockRegistry getBlock(int x, int y, int z) {
        SubChunk subChunk = null;
        if (subChunks.stream().anyMatch(subchunk ->
                subchunk.getCoordinates().getX().equals(x) &&
                subchunk.getCoordinates().getY().equals(y) &&
                subchunk.getCoordinates().getZ().equals(z)))
            subChunk = subChunks.stream()
                    .filter(subchunk ->
                            subchunk.getCoordinates().getX().equals(x) &&
                            subchunk.getCoordinates().getY().equals(y) &&
                            subchunk.getCoordinates().getZ().equals(z))
                    .findFirst()
                    .orElse(null);
        if (subChunk == null) return BlockRegistries.AIR;
        return subChunk.getBlock(x & 15, y & 15, z & 15);
    }
    // endregion
}