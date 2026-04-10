package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
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

    public void placeBlock(BlockCoordinates coordinates, BlockRegistry block) {
        getSubChunk(coordinates).placeBlock(CoordinatesConverter.toLocalSubChunk(coordinates), block);
    }

    public void digBlock(BlockCoordinates coordinates) {
        getSubChunk(coordinates).digBlock(CoordinatesConverter.toLocalSubChunk(coordinates));
    }

    public void generateNaturalSubChunk(SubChunkCoordinates subChunkCoordinates) {
        SubChunk interestedSubChunk = new SubChunk(subChunkCoordinates);
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    if (y == 15) interestedSubChunk.placeBlock(new LocalSubChunkCoordinates(x, y, z), BlockRegistries.GRASS);
                    else if (y > 12) interestedSubChunk.placeBlock(new LocalSubChunkCoordinates(x, y, z), BlockRegistries.DIRT);
                    else interestedSubChunk.placeBlock(new LocalSubChunkCoordinates(x, y, z), BlockRegistries.STONE);
                    //interestedSubChunk.placeBlock(new LocalSubChunkCoordinates(x, y, z), BlockRegistries.STONE);
                }
            }
        }
        subChunks.add(interestedSubChunk);
    }

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

    public void update(float delta) { subChunks.forEach(subChunk -> subChunk.update(delta)); }
    public void render() { subChunks.forEach(SubChunk::render); }
    public void delete() { subChunks.forEach(SubChunk::delete); }

    public ChunkCoordinates getCoordinates() { return coordinates; }
}