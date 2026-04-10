package dev.m4nd3l.craftmine.world;

import dev.m4nd3l.craftmine.coordinates.*;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.global.Settings;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.Camera;
import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.uniforms.IntUniform;
import dev.m4nd3l.craftmine.renderer.optimization.RenderingOptimization;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.json.WorldData;
import dev.m4nd3l.craftmine.world.gen.ChunkGenerator;
import dev.m4nd3l.craftmine.world.gen.TerrainGenerator;
import org.joml.Vector3f;

import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class World {
    private long glfwWindow;
    private ShaderProgram shader;
    private Texture blockAtlas;

    private WorldData data;
    private Map<ChunkCoordinates, Chunk> chunks;
    private ChunkCoordinates lastCenter;

    private MFile worldSavePosition;
    private MFile dataSavePosition;
    private MFile chunksSavePosition;

    private ChunkGenerator chunkGenerator;

    public World(long glfwWindow, String name, String seed) {
        this.glfwWindow = glfwWindow;
        this.shader = RenderingOptimization.shaders.getOrCreate(
                new MFile("assets", "shaders", "default.vert"),
                new MFile("assets", "shaders", "default.frag"));
        this.blockAtlas = new Texture(new MFile("assets", "textures", "blockAtlas.png"));

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
        }
        else chunk = generateChunk(coordinates);
        chunks.put(coordinates, chunk);
    }
    public void unloadChunk(ChunkCoordinates coordinates, boolean removeFromMap) {
        if (!chunks.containsKey(coordinates)) return;
        MFile chunkFile = new MFile(chunksSavePosition, coordinates + ".json");
        String content = Consts.gson.toJson(chunks.get(coordinates));
        if (chunkFile.exists()) chunkFile.writeString(content, false);
        else chunkFile.create(content);
        if (removeFromMap) chunks.remove(coordinates);
    }
    // endregion
    // region MAIN METHODS
    public void update(float delta) {
        data.getPlayer().processKeyboard(Input.keyboard, delta);
        data.getPlayer().processMouseMovement(Input.mouse, glfwWindow);
        data.getPlayer().updateMatrices();
        updateLoadedChunks(data.getPlayer().getEntityPosition());
        chunks.forEach((coordinates, chunk) -> chunk.update(delta));
    }
    public void render() {
        shader.bind();
        blockAtlas.bind();
        data.getPlayer().uploadUniforms(shader);
        shader.uploadUniform(new IntUniform("blockTexture", shader.getShaderID(), 0));
        chunks.forEach((coordinates, chunk) -> chunk.render());
        blockAtlas.unbind();
    }
    public void delete() {
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