package dev.m4nd3l.craftmine.renderer.world;

import dev.m4nd3l.craftmine.coordinates.BlockCoordinates;
import dev.m4nd3l.craftmine.coordinates.CoordinatesConverter;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.world.communication.WorldCommunication;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

public class SubChunkMesher {

    private static final float UV_EPSILON = 0.0005f;

    public static FloatArrayList generateMesh(SubChunkCoordinates coordinates, short[] blocks) {
        var vertices = new FloatArrayList(20000);
        var origin = CoordinatesConverter.toBlock(coordinates);

        for (int x = 0; x < Consts.SIZE; x++)
            for (int y = 0; y < Consts.SIZE; y++)
                for (int z = 0; z < Consts.SIZE; z++) {
                    short blockID = getBlockID(x, y, z, blocks);

                    if (isTransparent(blockID)) continue;

                    // Directions: Left, Right, Bottom, Top, Back, Front
                    checkAndAddFace(x, y, z, -1, 0, 0, 0, blockID, origin, blocks, vertices);
                    checkAndAddFace(x, y, z, 1, 0, 0, 1, blockID, origin, blocks, vertices);
                    checkAndAddFace(x, y, z, 0, -1, 0, 2, blockID, origin, blocks, vertices);
                    checkAndAddFace(x, y, z, 0, 1, 0, 3, blockID, origin, blocks, vertices);
                    checkAndAddFace(x, y, z, 0, 0, -1, 4, blockID, origin, blocks, vertices);
                    checkAndAddFace(x, y, z, 0, 0, 1, 5, blockID, origin, blocks, vertices);
                }

        return vertices;
    }

    private static void checkAndAddFace(int x, int y, int z, int dx, int dy, int dz, int face,
                                        short blockID, BlockCoordinates origin, short[] blocks, FloatArrayList vertices) {
        int nx = x + dx;
        int ny = y + dy;
        int nz = z + dz;

        short neighborID;

        if (nx >= 0 && nx < Consts.SIZE && ny >= 0 && ny < Consts.SIZE && nz >= 0 && nz < Consts.SIZE) {
            neighborID = blocks[nx + Consts.SIZE * (ny + Consts.SIZE * nz)];
        } else {
            int worldX = (int) (origin.getX() + nx);
            int worldY = (int) (origin.getY() + ny);
            int worldZ = (int) (origin.getZ() + nz);

            if (!WorldCommunication.isChunkAvailable(worldX, worldY, worldZ)) return;

            neighborID = WorldCommunication.getBlockID(worldX, worldY, worldZ);
        }

        if (isTransparent(neighborID)) {
            addFace(x, y, z, face, blockID, origin, vertices);
        }
    }

    private static void addFace(int x, int y, int z, int face, short blockID,
                                BlockCoordinates origin, FloatArrayList vertices) {

        BlockRegistry block = BlockRegistries.getBlock(blockID);
        var settings = block.getInstance().getSettings();
        var color = settings.getColor();

        float worldX = x + origin.getX();
        float worldY = y + origin.getY();
        float worldZ = z + origin.getZ();

        float r = (color != null) ? color.getR() : 1.0f;
        float g = (color != null) ? color.getG() : 1.0f;
        float b = (color != null) ? color.getB() : 1.0f;

        int texID;
        float nx = 0, ny = 0, nz = 0;

        switch (face) {
            case 0 -> { texID = block.getSideTextureID();   nx = -1; } // Left
            case 1 -> { texID = block.getSideTextureID();   nx =  1; } // Right
            case 2 -> { texID = block.getBottomTextureID(); ny = -1; } // Bottom
            case 3 -> { texID = block.getTopTextureID();    ny =  1; } // Top
            case 4 -> { texID = block.getSideTextureID();   nz = -1; } // Back
            case 5 -> { texID = block.getSideTextureID();   nz =  1; } // Front
            default -> texID = block.getSideTextureID();
        }

        float[] atlas = getUVs(texID);

        switch (face) {
            case 0 -> renderLeft(worldX, worldY, worldZ, nx, ny, nz, r, g, b, atlas, vertices);
            case 1 -> renderRight(worldX, worldY, worldZ, nx, ny, nz, r, g, b, atlas, vertices);
            case 2 -> renderBottom(worldX, worldY, worldZ, nx, ny, nz, r, g, b, atlas, vertices);
            case 3 -> renderTop(worldX, worldY, worldZ, nx, ny, nz, r, g, b, atlas, vertices);
            case 4 -> renderBack(worldX, worldY, worldZ, nx, ny, nz, r, g, b, atlas, vertices);
            case 5 -> renderFront(worldX, worldY, worldZ, nx, ny, nz, r, g, b, atlas, vertices);
        }
    }

    private static void renderTop(float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float[] a, FloatArrayList v) {
        addVertex(x, y + 1, z + 1, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y + 1, z + 1, nx, ny, nz, r, g, b, 1, 0, a, v);
        addVertex(x + 1, y + 1, z, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y + 1, z + 1, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y + 1, z, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y + 1, z, nx, ny, nz, r, g, b, 0, 1, a, v);
    }

    private static void renderBottom(float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float[] a, FloatArrayList v) {
        addVertex(x, y, z, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y, z, nx, ny, nz, r, g, b, 1, 0, a, v);
        addVertex(x + 1, y, z + 1, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y, z, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y, z + 1, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y, z + 1, nx, ny, nz, r, g, b, 0, 1, a, v);
    }

    private static void renderLeft(float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float[] a, FloatArrayList v) {
        addVertex(x, y, z, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x, y, z + 1, nx, ny, nz, r, g, b, 1, 0, a, v);
        addVertex(x, y + 1, z + 1, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y, z, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x, y + 1, z + 1, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y + 1, z, nx, ny, nz, r, g, b, 0, 1, a, v);
    }

    private static void renderRight(float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float[] a, FloatArrayList v) {
        addVertex(x + 1, y, z + 1, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y, z, nx, ny, nz, r, g, b, 1, 0, a, v);
        addVertex(x + 1, y + 1, z, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x + 1, y, z + 1, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y + 1, z, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x + 1, y + 1, z + 1, nx, ny, nz, r, g, b, 0, 1, a, v);
    }

    private static void renderBack(float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float[] a, FloatArrayList v) {
        addVertex(x + 1, y, z, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x, y, z, nx, ny, nz, r, g, b, 1, 0, a, v);
        addVertex(x, y + 1, z, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x + 1, y, z, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x, y + 1, z, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x + 1, y + 1, z, nx, ny, nz, r, g, b, 0, 1, a, v);
    }

    private static void renderFront(float x, float y, float z, float nx, float ny, float nz, float r, float g, float b, float[] a, FloatArrayList v) {
        addVertex(x, y, z + 1, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y, z + 1, nx, ny, nz, r, g, b, 1, 0, a, v);
        addVertex(x + 1, y + 1, z + 1, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y, z + 1, nx, ny, nz, r, g, b, 0, 0, a, v);
        addVertex(x + 1, y + 1, z + 1, nx, ny, nz, r, g, b, 1, 1, a, v);
        addVertex(x, y + 1, z + 1, nx, ny, nz, r, g, b, 0, 1, a, v);
    }

    private static void addVertex(float px, float py, float pz, float nx, float ny, float nz,
                                  float r, float g, float b, float u, float v,
                                  float[] atlas, FloatArrayList vertices) {
        vertices.add(px); vertices.add(py); vertices.add(pz);
        vertices.add(nx); vertices.add(ny); vertices.add(nz);
        vertices.add(r);  vertices.add(g);  vertices.add(b);
        vertices.add(u);  vertices.add(v);
        vertices.add(atlas[0]); vertices.add(atlas[1]); // uMin, vMin
        vertices.add(atlas[2]); vertices.add(atlas[3]); // uMax, vMax
    }

    private static float[] getUVs(int id) {
        float uMin = (id % (int) Consts.atlasWidthTiles) * Consts.stepU;
        float vMin = (id / (int) Consts.atlasWidthTiles) * Consts.stepV;

        return new float[]{
                uMin + UV_EPSILON,
                vMin + UV_EPSILON,
                uMin + Consts.stepU - UV_EPSILON,
                vMin + Consts.stepV - UV_EPSILON
        };
    }

    private static boolean isTransparent(short blockID) {
        return blockID == BlockRegistries.AIR.getId();
    }

    private static short getBlockID(int x, int y, int z, short[] blocks) {
        return blocks[x + Consts.SIZE * (y + Consts.SIZE * z)];
    }
}