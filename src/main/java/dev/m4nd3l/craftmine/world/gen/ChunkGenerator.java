package dev.m4nd3l.craftmine.world.gen;

import dev.m4nd3l.craftmine.coordinates.ChunkCoordinates;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.world.Chunk;
import dev.m4nd3l.craftmine.world.gen.biomes.Biome;

public class ChunkGenerator {
    private final TerrainGenerator terrainGenerator;

    public ChunkGenerator(String seed) { terrainGenerator = new TerrainGenerator(seed); }

    public void generate(Chunk chunk) {
        ChunkCoordinates coords = chunk.getCoordinates();
        int worldX = coords.getX() * 16;
        int worldZ = coords.getZ() * 16;

        for (int x = 0; x < 16; x++)
            for (int z = 0; z < 16; z++) {
                int surfaceY = terrainGenerator.getApproxAltitude(worldX + x, worldZ + z);

                Biome biome = terrainGenerator.getBiome(worldX + x, surfaceY, worldZ + z);

                int depth = 0;
                for (int y = 255; y >= 0; y--) {
                    float density = terrainGenerator.getDensity(worldX + x, y, worldZ + z);

                    if (density <= 0) {
                        depth = 0;
                        //if(y < 62) chunk.placeBlock(worldX + x, y, worldZ + z, BlockRegistries.WATER.getIdShort());
                        continue;
                    }
                    short blockID = terrainGenerator.determineBlock(depth, biome);

                    chunk.placeBlock(
                            worldX + x, y, worldZ + z,
                            BlockRegistries.getBlock(blockID)
                    );
                    depth++;
                }
            }

    }
}