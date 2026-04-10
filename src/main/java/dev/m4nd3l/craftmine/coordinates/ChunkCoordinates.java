package dev.m4nd3l.craftmine.coordinates;

public class ChunkCoordinates extends Coordinates<Integer> {
    // Actual chunk in world coordinates
    public ChunkCoordinates(int x, int z) { super(x, 0, z);}

    @Override
    public String toString() { return String.format("%d_%d", getX(), getZ()); }
}