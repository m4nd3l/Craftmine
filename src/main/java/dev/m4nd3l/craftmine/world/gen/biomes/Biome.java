package dev.m4nd3l.craftmine.world.gen.biomes;

public class Biome {
    private final String name;
    private final short surfaceBlock;
    private final short underSurfaceBlock;
    private final float minTemperature;
    private final float minHumidity;
    private final float minHeight;

    public Biome(String name, short surface, short under, float temp, float humid, float height) {
        this.name = name;
        this.surfaceBlock = surface;
        this.underSurfaceBlock = under;
        this.minTemperature = temp;
        this.minHumidity = humid;
        this.minHeight = height;
    }

    public float getMinHumidity() { return minHumidity; }
    public float getMinTemperature() { return minTemperature; }
    public float getMinHeight() { return minHeight; }
    public short getSurfaceBlock() { return surfaceBlock; }
    public short getUnderSurfaceBlock() { return underSurfaceBlock; }
    public String getName() { return name; }

    @Override
    public String toString() { return "Biome: " + name; }
}
