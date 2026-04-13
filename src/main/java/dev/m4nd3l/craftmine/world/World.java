package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.global.Settings;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.Camera;
import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.uniforms.IntUniform;
import dev.m4nd3l.craftmine.renderer.optimization.RenderingOptimization;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.json.WorldData;
import dev.m4nd3l.craftmine.renderer.world.SubChunkMesher;
import dev.m4nd3l.craftmine.util.Mix;
import dev.m4nd3l.craftmine.world.communication.Communication;
import dev.m4nd3l.craftmine.world.gen.ChunkGenerator;
import dev.m4nd3l.craftmine.world.gen.TerrainGenerator;
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

    private MFile worldSavePosition;
    private MFile dataSavePosition;
    private MFile chunksSavePosition;

    private ChunkGenerator chunkGenerator;

    private Queue<Mix<SubChunkCoordinates, Runnable>> meshTaskQueue;
    private Queue<SubChunkCoordinates> readyToUpload;
    private Set<SubChunkCoordinates> activeTasks = ConcurrentHashMap.newKeySet();
    private Thread meshWorkerThread;
    private volatile boolean running = true;

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
        if (chunks == null) chunks = new HashMap<>();

        chunkGenerator = new ChunkGenerator(new TerrainGenerator(seed));

        this.readyToUpload = new ConcurrentLinkedQueue<>();
        this.meshTaskQueue = new ConcurrentLinkedQueue<>();
        this.activeTasks = new HashSet<>();
        this.meshWorkerThread = new Thread(() -> {
            while (running) {
                var mix = meshTaskQueue.poll();
                if (mix != null) {
                    var task = mix.getV2();
                    if (task != null) task.run();
                    else try { Thread.sleep(10); } catch (InterruptedException e) { break; }
                } else try { Thread.sleep(10); } catch (InterruptedException e) { break; }
            }
        }, "Mesh-Worker");
        this.meshWorkerThread.start();

        updateLoadedChunks(data.getPlayer().getEntityPosition());
    }

    public World(long glfwWindow, String name) {
        this(glfwWindow, name, String.valueOf(Math.random() * 101108356L));
    }

    // region INTERACTION
    public void placeBlock(BlockCoordinates coordinates, BlockRegistry block) { getChunk(coordinates).placeBlock(coordinates, block); }
    public void digBlock(BlockCoordinates coordinates) { getChunk(coordinates).digBlock(coordinates); }
    // endregion
    // region WORLD GEN
    public Chunk generateChunk(ChunkCoordinates coordinates) {
        Chunk chunk = new Chunk(coordinates);
        chunkGenerator.generate(chunk);
        return chunk;
    }
    // endregion
    // region MEMORY MANAGEMENT
    public void updateLoadedChunks(EntityCoordinates center) {
        var centerChunk = CoordinatesConverter.toChunk(center);
        if (centerChunk.equals(lastCenter)) return;
        lastCenter = centerChunk;
        int renderDistance = Settings.settings.getRenderDistance();

        Set<ChunkCoordinates> allowedCoords = new HashSet<>();

        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int z = -renderDistance; z <= renderDistance; z++) {
                var newCoords = new ChunkCoordinates(centerChunk.getX() + x, centerChunk.getZ() + z);
                allowedCoords.add(newCoords);
                loadChunk(newCoords);
            }
        }

        chunks.entrySet().removeIf(entry -> {
            if (allowedCoords.contains(entry.getKey())) return false;
            unloadChunk(entry.getKey(), false);
            return true;
        });
    }
    public void loadChunk(ChunkCoordinates coordinates) {
        if (chunks.containsKey(coordinates)) return;
        MFile chunkFile = new MFile(chunksSavePosition, coordinates + ".json");
        Chunk chunk;
        if (chunkFile.exists()) {
            chunk = Consts.gson.fromJson(chunkFile.readString(), Chunk.class);
            chunk.loadAfterInit();
        } else chunk = generateChunk(coordinates);
        chunks.put(coordinates, chunk);
    }
    public void unloadChunk(ChunkCoordinates coordinates, boolean removeFromMap) {
        if (!chunks.containsKey(coordinates)) return;
        MFile chunkFile = new MFile(chunksSavePosition, coordinates + ".json");
        String content = Consts.gson.toJson(chunks.get(coordinates));
        if (chunkFile.exists()) chunkFile.writeString(content, false);
        else chunkFile.create(content);
        chunks.get(coordinates).delete();
        if (removeFromMap) chunks.remove(coordinates);
    }
    // endregion
    // region COMMUNICATION
    public void getCommunication(SubChunkCoordinates coordinates, Communication communication) {
        switch (communication) {
            case REMESH_REQUEST: remeshRequest(coordinates);
            default: break;
        }
    }
    // endregion
    // region RENDERING
    private void remeshRequest(SubChunkCoordinates coordinates) {
        if (activeTasks.contains(coordinates)) return;
        Chunk interested = chunks.get(CoordinatesConverter.toChunk(coordinates));
        if (interested == null) return;
        activeTasks.add(coordinates);
        SubChunk subChunk = interested.getSubChunk(coordinates);
        meshTaskQueue.add(new Mix<>(coordinates, () -> {
            var vertices = SubChunkMesher.generateMesh(coordinates, subChunk.getBlocks());
            activeTasks.remove(coordinates);
            subChunk.newMesh(vertices);
            readyToUpload.add(coordinates);
        }));
    }
    // endregion
    // region MAIN METHODS
    public void update(float delta) {
        data.getPlayer().processKeyboard(Input.keyboard, delta);
        data.getPlayer().processMouseMovement(Input.mouse, glfwWindow);
        data.getPlayer().updateMatrices();
        updateLoadedChunks(data.getPlayer().getEntityPosition());
        chunks.forEach((_, chunk) -> chunk.update(delta));
        for (int i = 0; i < 5; i++) {
            SubChunkCoordinates coordinates = readyToUpload.poll();
            if (coordinates == null) continue;
            getChunk(coordinates).upload(coordinates);
        }
    }

    public void render() {
        shader.bind();
        Consts.texture.bind();
        data.getPlayer().uploadUniforms(shader);
        shader.uploadUniform(new IntUniform("blockTexture", shader.getShaderID(), 0));
        chunks.forEach((_, chunk) -> chunk.render());
        Consts.texture.unbind();
    }

    public void delete() {
        running = false;
        meshWorkerThread.interrupt();
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
    public Chunk getChunk(Coordinates coordinates) {
        ChunkCoordinates chunkCoordinates = CoordinatesConverter.toChunk(coordinates);
        if (chunks.entrySet().stream().anyMatch(chunkEntry -> chunkEntry.getKey().equals(chunkCoordinates)))
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