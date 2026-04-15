package dev.m4nd3l.craftmine.world;

import com.sun.jdi.event.StepEvent;
import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.global.Settings;
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
import dev.m4nd3l.craftmine.util.Mix;
import dev.m4nd3l.craftmine.world.communication.Communication;
import dev.m4nd3l.craftmine.world.gen.ChunkGenerator;
import dev.m4nd3l.craftmine.world.gen.TerrainGenerator;
import org.joml.Vector3f;

import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    private BlockingQueue<Mix<SubChunkCoordinates, Runnable>> meshTaskQueue1;
    private BlockingQueue<Mix<SubChunkCoordinates, Runnable>> meshTaskQueue2;
    private Queue<SubChunkCoordinates> readyToUpload;
    private Set<SubChunkCoordinates> activeTasks1;
    private Set<SubChunkCoordinates> activeTasks2;
    private Thread meshWorkerThread1;
    private Thread meshWorkerThread2;
    private volatile boolean running = true, is1 = true;

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

        chunkGenerator = new ChunkGenerator(seed);

        toLoad = new ConcurrentLinkedQueue<>();

        this.readyToUpload = new ConcurrentLinkedQueue<>();
        this.meshTaskQueue1 = new LinkedBlockingQueue<>();
        this.meshTaskQueue2 = new LinkedBlockingQueue<>();
        this.activeTasks1 = ConcurrentHashMap.newKeySet();
        this.activeTasks2 = ConcurrentHashMap.newKeySet();
        this.meshWorkerThread1 = new Thread(() -> {
            while (running) {
                try {
                    var mix = meshTaskQueue1.take();
                    var task = mix.getV2();
                    if (task == null) continue;
                    task.run();
                } catch (InterruptedException e) { break; }
            }
        }, "Mesh-Worker1");
        this.meshWorkerThread1.start();

        this.meshWorkerThread2 = new Thread(() -> {
            while (running) {
                try {
                    var mix = meshTaskQueue2.take();
                    var task = mix.getV2();
                    if (task == null) continue;
                    task.run();
                } catch (InterruptedException e) { break; }
            }
        }, "Mesh-Worker2");
        this.meshWorkerThread2.start();

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
        if (centerChunk.equals(lastCenter)) {
            if (toLoad.isEmpty()) return;
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

        lastCenter = centerChunk;
        int renderDistance = Settings.settings.getRenderDistance();

        Set<ChunkCoordinates> allowedCoords = new HashSet<>();

        for (int x = -renderDistance; x <= renderDistance; x++)
            for (int z = -renderDistance; z <= renderDistance; z++) {
                var coordinates = new ChunkCoordinates(centerChunk.getX() + x, centerChunk.getZ() + z);
                allowedCoords.add(coordinates);
                if (!chunks.containsKey(coordinates)) toLoad.add(coordinates);
            }

        toLoad.removeIf(coordinates -> !allowedCoords.contains(coordinates));

        chunks.entrySet().removeIf(entry -> {
            if (allowedCoords.contains(entry.getKey())) return false;
            unloadChunk(entry.getKey(), false);
            return true;
        });

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
        var tasks = is1 ? activeTasks1 : activeTasks2;
        var queue = is1 ? meshTaskQueue1 : meshTaskQueue2;
        if (tasks.contains(coordinates)) return;
        Chunk interested = chunks.get(CoordinatesConverter.toChunk(coordinates));
        if (interested == null) return;
        tasks.add(coordinates);
        SubChunk subChunk = interested.getSubChunk(coordinates);
        queue.add(new Mix<>(coordinates, () -> {
            var vertices = SubChunkMesher.generateMesh(coordinates, subChunk.getBlocks());
            tasks.remove(coordinates);
            subChunk.newMesh(vertices);
            readyToUpload.add(coordinates);
        }));
        is1 = !is1;
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
        for (int i = 0; i < 2; i++) {
            SubChunkCoordinates coords = readyToUpload.poll();
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
        running = false;

        meshWorkerThread1.interrupt();
        meshTaskQueue1.clear();
        activeTasks1.clear();

        meshWorkerThread2.interrupt();
        meshTaskQueue2.clear();
        activeTasks2.clear();

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