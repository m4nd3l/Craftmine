package dev.m4nd3l.craftmine.world.gen;

import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.world.gen.biomes.Biome;
import dev.m4nd3l.craftmine.world.gen.noise.*;

public class TerrainGenerator {
    private String seed;
    private int intSeed;
    private FastNoiseLite noise;
    private BiomeProvider biomeProvider;

    public TerrainGenerator(String seed) {
        this.seed = seed;
        this.intSeed = seed.hashCode();

        biomeProvider = new BiomeProvider(intSeed);

        noise = new FastNoiseLite(intSeed);
        noise.setNoiseType(NoiseType.OpenSimplex2);
        noise.setFractalType(FractalType.FBm);

        noise.setFrequency(0.01f);
        noise.setFractalOctaves(4);
        noise.setFractalLacunarity(2.0f);
        noise.setFractalGain(0.4f);
    }

    public float getDensity(int x, int y, int z) {
        float n = noise.getNoise((float) x, (float) y, (float) z);
        float heightGradient = (y - 64) / 40.0f;

        return n - heightGradient;
    }

    public int getApproxAltitude(int x, int z) {
        for (int y = 255; y > 0; y--) {
            if (getDensity(x, y, z) > 0) return y;
        }
        return 64;
    }

    public Biome getBiome(int x, int y, int z) { return biomeProvider.getBiome(x, y, z); }

    public short determineBlock(int depth, Biome biome) {
        if (depth == 0) return biome.getSurfaceBlock();
        if (depth < 4) return biome.getUnderSurfaceBlock();
        return BlockRegistries.STONE.getIdShort();
    }

    public String getSeed() { return seed; }
    public int getIntSeed() { return intSeed; }
}
