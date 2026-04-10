package dev.m4nd3l.craftmine.world.gen;

import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.world.gen.noise.*;

public class TerrainGenerator {
    private String seed;
    private int intSeed;
    private FastNoiseLite noise;

    public TerrainGenerator(String seed) {
        this.seed = seed;
        this.intSeed = seed.hashCode();

        noise = new FastNoiseLite(intSeed);
        noise.setNoiseType(NoiseType.OpenSimplex2);
        noise.setFractalType(FractalType.FBm);

        noise.setFrequency(0.01f);
        noise.setFractalOctaves(4);
        noise.setFractalLacunarity(2.0f);
        noise.setFractalGain(0.4f);
    }

    public int getHeight(int x, int z) {
        float n = noise.getNoise((float) x, 0.0f, (float) z);
        float normalized = (n + 1) / 2f;
        float smooth = (float) Math.pow(normalized, 1.5);

        return (int) (smooth * 40) + 63;
    }

    public short getBlockAt(int x, int y, int z, int height) {
        if (y > height) return 0;
        if (y == height) return BlockRegistries.GRASS.getIdShort();
        if (y > height - 4) return BlockRegistries.DIRT.getIdShort();
        return BlockRegistries.STONE.getIdShort();
    }
}
