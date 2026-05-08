package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.Camera;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Chunk implements Iterable<SubChunk> {
    private List<SubChunk> subChunks;
    private ChunkCoordinates coordinates;

    public Chunk(ChunkCoordinates coordinates) {
        subChunks = new CopyOnWriteArrayList<>();
        this.coordinates = coordinates;
    }

    public Chunk() {}

    public void loadAfterInit() {
        new ArrayList<>(subChunks).forEach(SubChunk::postLoadInit);
        ensureThreadSafety();
    }

    public void upload(SubChunkCoordinates coordinates) {
        SubChunk subChunk = getSubChunk(coordinates);
        if (subChunk == null) return;
        subChunk.uploadToGPU();
    }

    // region INTERACTION
    public void placeBlock(int x, int y, int z, BlockRegistry block) { placeBlock(x, y, z, block, true); }

    public void placeBlock(int x, int y, int z, BlockRegistry block, boolean setDirty) {
        getSubChunk(x, y, z).placeBlock(x & 15, y & 15, z & 15, block, setDirty);
    }

    public void digBlock(int x, int y, int z) {
        getSubChunk(x, y, z).digBlock(x & 15, y & 15, z & 15);
    }
    // endregion
    // region MAIN METHODS
    public void update(float delta) { ensureThreadSafety(); subChunks.forEach(subChunk -> subChunk.update(delta)); }
    public void render(Camera camera) { ensureThreadSafety(); subChunks.forEach(subChunk -> subChunk.render(camera)); }
    public void delete() { subChunks.forEach(SubChunk::delete); }
    // endregion
    // region HELPERS
    public void ensureThreadSafety() {
        if (!(this.subChunks instanceof CopyOnWriteArrayList)) this.subChunks = new CopyOnWriteArrayList<>(this.subChunks);
    }

    public ChunkCoordinates getCoordinates() { return coordinates; }
    public SubChunk getSubChunk(Coordinates coordinates) {
        ensureThreadSafety();
        SubChunkCoordinates subChunkCoordinates = CoordinatesConverter.toSubChunk(coordinates);
        if (subChunks.stream().anyMatch(subChunk -> subChunk.getCoordinates().equals(subChunkCoordinates)))
            return subChunks.stream()
                    .filter(subChunk -> subChunk.getCoordinates().equals(subChunkCoordinates))
                    .findFirst()
                    .orElse(null);
        SubChunk newChunk = new SubChunk(subChunkCoordinates);
        subChunks.add(newChunk);
        return newChunk;
    }

    public SubChunk getSubChunk(int x, int y, int z) {
        ensureThreadSafety();
        SubChunkCoordinates subCoords = CoordinatesConverter.toSubChunk(new BlockCoordinates(x, y, z));
        for (SubChunk sc : subChunks) if (sc.getCoordinates().equals(subCoords)) return sc;
        SubChunk newSc = new SubChunk(subCoords);
        subChunks.add(newSc);
        return newSc;
    }

    public BlockRegistry getBlock(int x, int y, int z) {
        ensureThreadSafety();
        List<SubChunk> currentSubChunks = this.subChunks;

        SubChunk target = null;
        for (SubChunk sc : currentSubChunks)
            if (sc.getCoordinates().getX() == (x >> 4) &&
                    sc.getCoordinates().getY() == (y >> 4) &&
                    sc.getCoordinates().getZ() == (z >> 4)) {
                target = sc;
                break;
            }

        if (target == null) return BlockRegistries.AIR;
        return target.getBlock(x & 15, y & 15, z & 15);
    }

    @NotNull
    @Override
    public Iterator<SubChunk> iterator() { return subChunks.iterator(); }
    @Override
    public void forEach(Consumer<? super SubChunk> action) { subChunks.forEach(action); }
    @Override
    public Spliterator<SubChunk> spliterator() { return subChunks.spliterator(); }
    // endregion
}