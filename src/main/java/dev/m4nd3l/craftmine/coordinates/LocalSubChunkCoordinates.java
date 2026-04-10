package dev.m4nd3l.craftmine.coordinates;

public class LocalSubChunkCoordinates extends Coordinates<Integer> {
    // Block in subchunk coordinates (max x: 16, y: 16, z: 16)
    public LocalSubChunkCoordinates(int x, int y, int z) { super(x, y, z);}
}