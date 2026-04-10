package dev.m4nd3l.craftmine.world.gen;

import dev.m4nd3l.craftmine.coordinates.BlockCoordinates;
import dev.m4nd3l.craftmine.coordinates.ChunkCoordinates;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.world.Chunk;

public class ChunkGenerator {
    private final TerrainGenerator terrainGenerator;

    public ChunkGenerator(TerrainGenerator terrainGenerator) {
        this.terrainGenerator = terrainGenerator;
    }

    public void generate(Chunk chunk) {
        ChunkCoordinates coords = chunk.getCoordinates();
        int worldX = coords.getX() * 16;
        int worldZ = coords.getZ() * 16;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = terrainGenerator.getHeight(worldX + x, worldZ + z);
                for (int y = 0; y < 256; y++) {
                    short blockID = terrainGenerator.getBlockAt(worldX + x, y, worldZ + z, height);

                    if (blockID != 0)
                        chunk.placeBlock(
                                new BlockCoordinates(worldX + x, y, worldZ + z),
                                BlockRegistries.getBlock(blockID)
                        );
                }
            }
        }
    }
}