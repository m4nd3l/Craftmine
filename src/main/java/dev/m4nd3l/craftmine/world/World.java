package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.global.Settings;
import dev.m4nd3l.craftmine.multithreading.MultiThread;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.Camera;
import dev.m4nd3l.craftmine.renderer.input.KeyboardKeys;
import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.uniforms.IntUniform;
import dev.m4nd3l.craftmine.renderer.optimization.RenderingOptimization;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.json.WorldData;
import dev.m4nd3l.craftmine.renderer.world.SubChunkMesher;
import dev.m4nd3l.craftmine.world.communication.Communication;
import dev.m4nd3l.craftmine.world.gen.ChunkGenerator;
import org.joml.Vector3f;

import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class World {
    private long glfwWindow;
    private ShaderProgram shader;

    private WorldData data;
    private Map<ChunkCoordinates, Chunk> chunks;
    private ChunkCoordinates lastCenter;

    private Queue<ChunkCoordinates> toLoad;

    private MFile worldSavePosition;
    private MFile dataSavePosition;
    private MFile chunksSavePosition;

    private ChunkGenerator chunkGenerator;

    private MultiThread<SubChunkCoordinates> meshingThreads;
    private MultiThread<ChunkCoordinates> loadingChunkThreads;
    private MultiThread<ChunkCoordinates> unloadingChunkThreads;

    public World(long glfwWindow, String name, String seed) {
        this.glfwWindow = glfwWindow;
        this.shader = RenderingOptimization.shaders.getOrCreate(
                new MFile("assets", "shaders", "default.vert"),
                new MFile("assets", "shaders", "default.frag"));

        this.worldSavePosition = new MFile("data", "saves", name);
        this.chunksSavePosition = new MFile(worldSavePosition, "chunks");
        this.dataSavePosition = new MFile(worldSavePosition, "data.json");

        if (worldSavePosition.exists()) data = Consts.gson.fromJson(dataSavePosition.readString(), WorldData.class);
        else try {
                Files.createDirectories(worldSavePosition.getFile().toPath());
                Files.createDirectories(chunksSavePosition.getFile().toPath());
                new MFile(worldSavePosition, "data.json").writeString("", false);
            } catch (Exception e) { System.err.println(e); }

        if (data == null) data = new WorldData()
                .setPlayer(new Camera(70.0f, 0.1f, 1000.0f, 1920, 1080,
                        new Vector3f(0.0f, 0f, 0f)))
                .setWorldName(name)
                .setWorldSeed(seed);
        if (chunks == null) chunks = new ConcurrentHashMap<>();

        chunkGenerator = new ChunkGenerator(seed);

        toLoad = new ConcurrentLinkedQueue<>();

        meshingThreads = new MultiThread<>(8);
        loadingChunkThreads = new MultiThread<>(5);
        unloadingChunkThreads = new MultiThread<>(5);

        updateLoadedChunks(data.getPlayer().getEntityPosition(), -1);
    }

    public World(long glfwWindow, String name) { this(glfwWindow, name, String.valueOf(Math.random() * 101108356L)); }

    // region INTERACTION
    public void placeBlock(BlockCoordinates coordinates, BlockRegistry block) {
        getChunk(coordinates).placeBlock(coordinates.getX(), coordinates.getY(), coordinates.getZ(), block);
    }
    public void digBlock(BlockCoordinates coordinates) {
        getChunk(coordinates).digBlock(coordinates.getX(), coordinates.getY(), coordinates.getZ());
    }
    // endregion
    // region WORLD GEN
    public Chunk generateChunk(ChunkCoordinates coordinates) {
        Chunk chunk = new Chunk(coordinates);
        chunkGenerator.generate(chunk);
        return chunk;
    }
    // endregion
    // region MEMORY MANAGEMENT
    public void updateLoadedChunks(EntityCoordinates center, int limit) {
        var centerChunk = CoordinatesConverter.toChunk(center);

        // Se il giocatore non si è spostato di un intero chunk, continuiamo a svuotare la coda esistente
        if (centerChunk.equals(lastCenter)) {
            loadLimitedChunks(limit);
            return;
        }

        lastCenter = centerChunk;
        int renderDistance = Settings.settings.getRenderDistance();
        Set<ChunkCoordinates> allowedCoords = new HashSet<>();
        List<ChunkCoordinates> newSpiralList = new ArrayList<>();

        // 1. Definiamo l'area circondariale
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                var coordinates = new ChunkCoordinates(centerChunk.getX() + x, centerChunk.getZ() + z);
                allowedCoords.add(coordinates);

                // Aggiungiamo alla lista solo se il chunk non è già in memoria
                if (!chunks.containsKey(coordinates)) {
                    newSpiralList.add(coordinates);
                }
            }
        }

        // 2. Ordinamento Radiale (Espansione dal centro)
        newSpiralList.sort(Comparator.comparingInt(c -> {
            int dx = c.getX() - centerChunk.getX();
            int dz = c.getZ() - centerChunk.getZ();
            return (dx * dx + dz * dz); // Distanza quadratica per massima velocità CPU
        }));

        // 3. Reset Strategico della Coda
        // Cancelliamo la vecchia coda per evitare che i thread carichino chunk lontani
        toLoad.clear();
        toLoad.addAll(newSpiralList);

        // 4. Rimozione dei chunk fuori distanza[cite: 13]
        chunks.entrySet().removeIf(entry -> {
            if (allowedCoords.contains(entry.getKey())) return false;
            unloadChunk(entry.getKey(), false); // Salvataggio asincrono[cite: 12, 13]
            return true;
        });

        loadLimitedChunks(limit);
    }

    public void loadChunk(ChunkCoordinates coordinates) {
        if (coordinates == null || chunks.containsKey(coordinates)) return;

        MFile chunkFile = new MFile(chunksSavePosition, coordinates + ".json");
        if (chunkFile.exists()) {
            loadingChunkThreads.addToQueue(coordinates, () -> {
                String json = chunkFile.readString();
                if (json == null || json.isEmpty()) return;

                Chunk chunk = Consts.gson.fromJson(json, Chunk.class);
                if (chunk != null) {
                    chunk.loadAfterInit();
                    chunks.put(coordinates, chunk);
                }
            });
        } else {
            loadingChunkThreads.addToQueue(coordinates, () -> {
                Chunk chunk = generateChunk(coordinates);
                if (chunk != null) {
                    chunks.put(coordinates, chunk);
                }
            });
        }
    }

    public void unloadChunk(ChunkCoordinates coordinates, boolean removeFromMap) {
        if (!chunks.containsKey(coordinates)) return;
        MFile chunkFile = new MFile(chunksSavePosition, coordinates + ".json");
        if (chunkFile.exists()) unloadingChunkThreads.addToQueue(coordinates, () -> chunkFile.writeString(Consts.gson.toJson(chunks.get(coordinates)), false));
        else unloadingChunkThreads.addToQueue(coordinates, () -> chunkFile.create(Consts.gson.toJson(chunks.get(coordinates))));
        chunks.get(coordinates).delete();
        if (removeFromMap) chunks.remove(coordinates);
    }
    // endregion
    // region COMMUNICATION
    public void getCommunication(SubChunkCoordinates coordinates, Communication communication) {
        if (Objects.requireNonNull(communication) == Communication.REMESH_REQUEST) {
            remeshRequest(coordinates);
        }
    }

    public BlockRegistry getBlockRegistryRequest(int x, int y, int z) {
        Chunk chunk = null;
        if (chunks.entrySet().stream().anyMatch(chunkEntry ->
                chunkEntry.getKey().getX().equals(x) &&
                        chunkEntry.getKey().getY().equals(y) &&
                        chunkEntry.getKey().getZ().equals(z)))
            chunk = chunks.entrySet().stream()
                    .filter(chunkEntry ->
                            chunkEntry.getKey().getX().equals(x) &&
                                    chunkEntry.getKey().getY().equals(y) &&
                                    chunkEntry.getKey().getZ().equals(z))
                    .findFirst()
                    .orElse(null)
                    .getValue();
        if (chunk == null) return BlockRegistries.AIR;
        return chunk.getBlock(x, y, z);
    }
    // endregion
    // region RENDERING
    private void remeshRequest(SubChunkCoordinates coordinates) {
        Chunk interested = chunks.get(CoordinatesConverter.toChunk(coordinates));
        if (interested == null) return;
        SubChunk subChunk = interested.getSubChunk(coordinates);
        meshingThreads.addToQueue(coordinates, () -> subChunk.newMesh(SubChunkMesher.generateMesh(coordinates, subChunk.getBlocks())));
    }
    // endregion
    // region MAIN METHODS
    public void update(float delta) {
        data.getPlayer().processKeyboard(Input.keyboard, delta);
        data.getPlayer().processMouseMovement(Input.mouse, glfwWindow);
        data.getPlayer().updateMatrices();

        if (Input.keyboard.isControlDown() &&
            Input.keyboard.isKeyPressed(KeyboardKeys.K) &&
            Main.craftmine.debug) data.getPlayer().frustumFreeze = !data.getPlayer().frustumFreeze;

        updateLoadedChunks(data.getPlayer().getEntityPosition(), 10);
        chunks.forEach((_, chunk) -> chunk.update(delta));

        meshingThreads.update();
        loadingChunkThreads.update();
        unloadingChunkThreads.update();

        for (int i = 0; i < 10; i++) {
            SubChunkCoordinates coords = meshingThreads.pollReady();
            if (coords == null) break;
            Chunk chunk = chunks.get(CoordinatesConverter.toChunk(coords));
            if (chunk != null) chunk.upload(coords);
        }
    }

    public void render() {
        if (!data.getPlayer().frustumFreeze) data.getPlayer().updateFrustum();
        shader.bind();
        Consts.texture.bind();
        data.getPlayer().uploadUniforms(shader);
        shader.uploadUniform(new IntUniform("blockTexture", shader.getShaderID(), 0));
        chunks.forEach((_, chunk) -> chunk.render(data.getPlayer()));
        Consts.texture.unbind();
    }

    public void delete() {
        meshingThreads.delete();

        chunks.forEach((coordinates, chunk) -> {
            unloadChunk(coordinates, false);
            chunk.delete();
        });

        dataSavePosition.writeString(Consts.gson.toJson(data), false);
        shader.unbind();
        shader.delete();
        chunks.clear();
    }
    // endregion
    // region HELPERS
    private void loadLimitedChunks(int limit) {
        if (limit < 0)
            while (true) {
                loadChunk(toLoad.poll());
                if (toLoad.isEmpty()) return;
            }

        for (int i = 0; i < limit; i++) {
            loadChunk(toLoad.poll());
            if (toLoad.isEmpty()) return;
        }
    }

    public Chunk getChunk(Coordinates coordinates) {
        ChunkCoordinates chunkCoordinates = CoordinatesConverter.toChunk(coordinates);
        if (chunks.containsKey(chunkCoordinates))
            return chunks.entrySet().stream()
                    .filter(chunkEntry -> chunkEntry.getKey().equals(chunkCoordinates))
                    .findFirst()
                    .orElse(null)
                    .getValue();
        Chunk newChunk = new Chunk(chunkCoordinates);
        chunks.put(chunkCoordinates, newChunk);
        return newChunk;
    }
    // endregion
}