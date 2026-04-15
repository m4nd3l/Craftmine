package dev.m4nd3l.craftmine.coordinates;

import java.util.function.Function;

public class CoordinatesConverter {

    public static BlockCoordinates toBlock(Coordinates<?> source) {
        if (source instanceof BlockCoordinates b) return b;
        if (source instanceof EntityCoordinates e) return transform(e, f -> (int) Math.floor(f), BlockCoordinates::new);
        if (source instanceof ChunkCoordinates c) return transform(c, i -> i << 4, BlockCoordinates::new);
        if (source instanceof LocalSubChunkCoordinates s) return transform(s, i -> i << 4, BlockCoordinates::new);
        if (source instanceof SubChunkCoordinates s) return transform(s, i -> i << 4, BlockCoordinates::new);

        return handleUnsupported(source, "BlockCoordinates");
    }

    public static ChunkCoordinates toChunk(Coordinates<?> source) {
        if (source instanceof ChunkCoordinates c) return c;
        if (source instanceof BlockCoordinates b) return transform(b, i -> i >> 4, (x, _, z) -> new ChunkCoordinates(x, z));
        if (source instanceof EntityCoordinates e) return transform(e, f -> (int) Math.floor(f) >> 4, (x, _, z) -> new ChunkCoordinates(x, z));
        if (source instanceof SubChunkCoordinates e) return new ChunkCoordinates(e.getX(), e.getZ());

        return handleUnsupported(source, "ChunkCoordinates");
    }

    public static LocalSubChunkCoordinates toLocalSubChunk(Coordinates<?> source) {
        if (source instanceof LocalSubChunkCoordinates s) return s;
        if (source instanceof BlockCoordinates b) return transform(b, i -> i & 15, LocalSubChunkCoordinates::new);
        if (source instanceof EntityCoordinates e) return transform(e, f -> (int) Math.floor(f) & 15, LocalSubChunkCoordinates::new);

        return handleUnsupported(source, "SubChunkCoordinates");
    }

    public static SubChunkCoordinates toSubChunk(Coordinates<?> source) {
        if (source instanceof SubChunkCoordinates s) return s;
        if (source instanceof BlockCoordinates b) return transform(b, i -> i >> 4, SubChunkCoordinates::new);
        if (source instanceof EntityCoordinates e) return transform(e, f -> (int) Math.floor(f) >> 4, SubChunkCoordinates::new);

        return handleUnsupported(source, "SubChunkCoordinates");
    }

    public static EntityCoordinates toEntity(Coordinates<?> source) {
        if (source instanceof EntityCoordinates e) return e;
        if (source instanceof BlockCoordinates b) return transform(b, Integer::floatValue, EntityCoordinates::new);
        if (source instanceof ChunkCoordinates c) return transform(c, i -> (float)(i << 4), EntityCoordinates::new);

        return handleUnsupported(source, "EntityCoordinates");
    }

    // --- HELPER METHODS ---

    private static <S, D, SC extends Coordinates<S>, DC extends Coordinates<D>> DC transform(
            SC source,
            Function<S, D> operation,
            CoordinateFactory<D, DC> factory) {
        return factory.create(
                operation.apply(source.getX()),
                operation.apply(source.getY()),
                operation.apply(source.getZ())
        );
    }

    private static <R> R handleUnsupported(Coordinates<?> source, String target) {
        throw new IllegalArgumentException("Cannot convert " + source.getClass().getSimpleName() + " to " + target);
    }

    @FunctionalInterface
    interface CoordinateFactory<T, R> {
        R create(T x, T y, T z);
    }
}