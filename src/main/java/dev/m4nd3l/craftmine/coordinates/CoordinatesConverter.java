package dev.m4nd3l.craftmine.coordinates;

public class CoordinatesConverter {
    public static BlockCoordinates toBlock(Coordinates source) {
        return switch (source) {
            case BlockCoordinates b -> b;
            case EntityCoordinates e -> new BlockCoordinates(
                    (int) Math.floor(e.getX()),
                    (int) Math.floor(e.getY()),
                    (int) Math.floor(e.getZ())
            );
            case ChunkCoordinates c -> new BlockCoordinates((int) c.getX() << 4, 0, (int) c.getZ() << 4);
            case SubChunkCoordinates s -> new BlockCoordinates((int) s.getX() << 4, (int) s.getY() << 4, (int) s.getZ() << 4);
            case LocalSubChunkCoordinates l -> new BlockCoordinates((int) l.getX(), (int) l.getY(), (int) l.getZ());
            default -> throw new IllegalStateException("Unexpected value: " + source);
        };
    }

    public static ChunkCoordinates toChunk(Coordinates source) {
        return switch (source) {
            case ChunkCoordinates c -> c;
            case BlockCoordinates b -> new ChunkCoordinates((int) b.getX() >> 4, (int) b.getZ() >> 4);
            case EntityCoordinates e -> new ChunkCoordinates((int) Math.floor(e.getX()) >> 4, (int) Math.floor(e.getZ()) >> 4);
            case SubChunkCoordinates s -> new ChunkCoordinates((int) s.getX(), (int) s.getZ());
            case LocalSubChunkCoordinates l -> throw new UnsupportedOperationException("Cannot determine global chunk from local coordinates alone.");
            default -> throw new IllegalStateException("Unexpected value: " + source);
        };
    }

    public static SubChunkCoordinates toSubChunk(Coordinates source) {
        return switch (source) {
            case SubChunkCoordinates s -> s;
            case BlockCoordinates b -> new SubChunkCoordinates(
                    (int) b.getX() >> 4,
                    (int) b.getY() >> 4,
                    (int) b.getZ() >> 4
            );
            case EntityCoordinates e -> new SubChunkCoordinates(
                    (int) Math.floor(e.getX()) >> 4,
                    (int) Math.floor(e.getY()) >> 4,
                    (int) Math.floor(e.getZ()) >> 4
            );
            case ChunkCoordinates c -> new SubChunkCoordinates(
                    (int) c.getX(),
                    0,
                    (int) c.getZ()
            );

            case LocalSubChunkCoordinates l -> throw new UnsupportedOperationException("Local coordinates do not contain global SubChunk index information.");
            default -> throw new IllegalStateException("Unexpected value: " + source);
        };
    }

    public static LocalSubChunkCoordinates toLocalSubChunk(Coordinates source) {
        return switch (source) {
            case LocalSubChunkCoordinates l -> l;
            case BlockCoordinates b -> new LocalSubChunkCoordinates((int) b.getX() & 15, (int) b.getY() & 15, (int) b.getZ() & 15);
            case EntityCoordinates e -> new LocalSubChunkCoordinates(
                    (int) Math.floor(e.getX()) & 15,
                    (int) Math.floor(e.getY()) & 15,
                    (int) Math.floor(e.getZ()) & 15
            );
            default -> throw new IllegalArgumentException("Source type too coarse for local conversion");
        };
    }

    public static EntityCoordinates toEntity(Coordinates source) {
        if (source instanceof EntityCoordinates e) return e;

        BlockCoordinates b = toBlock(source);
        return new EntityCoordinates(b.getX(), b.getY(), b.getZ());
    }
}