package dev.m4nd3l.craftmine.world.gen;

import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.world.gen.biomes.Biome;

import java.util.ArrayList;
import java.util.List;

public class BiomeProvider {
    private FastNoiseLite tempNoise;
    private FastNoiseLite humidNoise;
    private List<Biome> biomes;

    public BiomeProvider(int seed) {
        biomes = new ArrayList<>();

        tempNoise = new FastNoiseLite(seed + 1);
        humidNoise = new FastNoiseLite(seed + 2);

        tempNoise.setFrequency(0.001f);
        humidNoise.setFrequency(0.001f);

        biomes.add(new Biome("Desert", BlockRegistries.SAND.getIdShort(), BlockRegistries.SANDSTONE.getIdShort(), 0.7f, 0.1f, 0.2f));
        biomes.add(new Biome("Plains", BlockRegistries.GRASS.getIdShort(), BlockRegistries.DIRT.getIdShort(), 0.5f, 0.5f, 0.3f));
        biomes.add(new Biome("Jungle", BlockRegistries.STONE.getIdShort(), BlockRegistries.DIRT.getIdShort(), 0.8f, 0.9f, 0.4f));
        biomes.add(new Biome("Tundra", BlockRegistries.DIRT.getIdShort(), BlockRegistries.DIRT.getIdShort(), 0.1f, 0.3f, 0.8f));
    }

    public Biome getBiome(int x, int y, int z) {
        float t = tempNoise.getNoise(x, 0.0f, z);
        float h = humidNoise.getNoise(x, 0.0f, z);
        float a = y / 255.0f;

        return closestBiome(t, h, a);
    }

    private Biome closestBiome(float noiseTemp, float noiseHumid, float noiseHeight) {
        Biome closest = null;
        float minDistance = Float.MAX_VALUE;

        for (Biome biome : biomes) {
            float dTemp = noiseTemp - biome.getMinTemperature();
            float dHumid = noiseHumid - biome.getMinHumidity();
            float dAltitude = noiseHeight - biome.getMinHeight();

            float distance = (dTemp * dTemp) + (dHumid * dHumid) + (dAltitude * dAltitude);

            if (distance < minDistance) {
                minDistance = distance;
                closest = biome;
            }
        }

        return closest;
    }
}
