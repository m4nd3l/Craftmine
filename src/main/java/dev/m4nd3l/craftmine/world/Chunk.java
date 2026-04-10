package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;

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

    // region INTERACTION
    public void placeBlock(BlockCoordinates coordinates, BlockRegistry block) {
        getSubChunk(coordinates).placeBlock(CoordinatesConverter.toLocalSubChunk(coordinates), block);
    }

    public void digBlock(BlockCoordinates coordinates) {
        getSubChunk(coordinates).digBlock(CoordinatesConverter.toLocalSubChunk(coordinates));
    }
    // endregion
    // region MAIN METHODS
    public void update(float delta) { subChunks.forEach(subChunk -> subChunk.update(delta)); }
    public void render() { subChunks.forEach(SubChunk::render); }
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
    // endregion
}