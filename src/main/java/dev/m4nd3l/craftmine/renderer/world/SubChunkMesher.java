package dev.m4nd3l.craftmine.renderer.world;

import dev.m4nd3l.craftmine.coordinates.CoordinatesConverter;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.global.Consts;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

public class SubChunkMesher {
    public static FloatArrayList generateMesh(SubChunkCoordinates coordinates, short[] blocks) {
        var vertices = new FloatArrayList();
        for (int axis = 0; axis < 3; axis++) runGreedyMesh(axis, blocks, coordinates, vertices);
        return vertices;
    }

    private static void runGreedyMesh(int d, short[] blocks, SubChunkCoordinates coordinates, FloatArrayList vertices) {
        int u = (d + 1) % 3;
        int v = (d + 2) % 3;
        int[] x = new int[3];
        int[] q = new int[3]; q[d] = 1;

        short[] mask = new short[Consts.SIZE * Consts.SIZE];

        for (x[d] = -1; x[d] < Consts.SIZE;) {
            int n = 0;
            for (x[v] = 0; x[v] < Consts.SIZE; x[v]++) {
                for (x[u] = 0; x[u] < Consts.SIZE; x[u]++) {
                    short b1 = (x[d] >= 0) ? getBlockID(x[0], x[1], x[2], blocks) : 0;
                    short b2 = (x[d] < Consts.SIZE - 1) ? getBlockID(x[0] + q[0], x[1] + q[1], x[2] + q[2], blocks) : 0;

                    boolean b1Transparent = isTransparent(b1);
                    boolean b2Transparent = isTransparent(b2);

                    if (b1Transparent == b2Transparent) mask[n++] = 0; // Both solid or both air: hide the face
                    else if (b2Transparent) mask[n++] = b1;            // b1 is solid, b2 is air: draw b1's front face
                    else mask[n++] = (short)-b2;                       // b2 is solid, b1 is air: draw b2's back face
                }
            }

            x[d]++;
            n = 0;

            for (int j = 0; j < Consts.SIZE; j++) {
                for (int i = 0; i < Consts.SIZE; ) {
                    if (mask[n] != 0) {
                        short currentID = mask[n];
                        int width, height;

                        for (width = 1; i + width < Consts.SIZE && mask[n + width] == currentID; width++);

                        boolean done = false;
                        for (height = 1; j + height < Consts.SIZE; height++) {
                            for (int k = 0; k < width; k++) {
                                if (mask[n + k + height * Consts.SIZE] != currentID) { done = true; break; }
                            }
                            if (done) break;
                        }

                        x[u] = i; x[v] = j;
                        int[] du = new int[3]; du[u] = width;
                        int[] dv = new int[3]; dv[v] = height;

                        addGreedyQuad(x, du, dv, currentID, d, width, height, coordinates, vertices);

                        for (int l = 0; l < height; l++) {
                            for (int k = 0; k < width; k++) mask[n + k + l * Consts.SIZE] = 0;
                        }
                        i += width; n += width;
                    } else {
                        i++; n++;
                    }
                }
            }
        }
    }

    private static void addGreedyQuad(int[] x, int[] du, int[] dv, short blockID, int axis, int width, int height,
                                      SubChunkCoordinates coordinates, FloatArrayList vertices) {
        boolean isBackFace = blockID < 0;
        BlockRegistry block = BlockRegistries.getBlock((short) Math.abs(blockID));
        var worldOffset = CoordinatesConverter.toBlock(coordinates);

        float x0 = x[0] + worldOffset.getX(),
              y0 = x[1] + worldOffset.getY(),
              z0 = x[2] + worldOffset.getZ();

        // 1. Setup Normals
        float nx = (axis == 0) ? (isBackFace ? -1 : 1) : 0;
        float ny = (axis == 1) ? (isBackFace ? -1 : 1) : 0;
        float nz = (axis == 2) ? (isBackFace ? -1 : 1) : 0;
        float[] norm = {nx, ny, nz};

        // 2. Texture & Color
        int texID = (axis == 1) ? (isBackFace ? block.getBottomTextureID() : block.getTopTextureID()) : block.getSideTextureID();
        float[] atlas = getUVs(texID);
        var color = block.getInstance().getSettings().getColor();
        float[] col = {(color != null) ? color.getR() : 1.0f, (color != null) ? color.getG() : 1.0f, (color != null) ? color.getB() : 1.0f};

        // 3. Define corners
        float[] p1 = {x0, y0, z0};
        float[] p2 = {x0 + du[0], y0 + du[1], z0 + du[2]};
        float[] p3 = {x0 + du[0] + dv[0], y0 + du[1] + dv[1], z0 + du[2] + dv[2]};
        float[] p4 = {x0 + dv[0], y0 + dv[1], z0 + dv[2]};

        float[] temp1 = p1; p1 = p4; p4 = temp1;
        float[] temp2 = p2; p2 = p3; p3 = temp2;

        float uMax, vMax;
        if (axis == 1) { // TOP/BOTTOM
            uMax = width;
            vMax = height;
            renderQuad(p1, p2, p3, p4, norm, col, uMax, vMax, atlas, isBackFace, vertices);
        } else if (axis == 0) { // X-SIDES
            uMax = height; // Z
            vMax = width;  // Y
            renderQuad(p4, p1, p2, p3, norm, col, uMax, vMax, atlas, isBackFace, vertices);
        } else { // Z-SIDES
            uMax = width;  // X
            vMax = height; // Y
            renderQuad(p4, p3, p2, p1, norm, col, uMax, vMax, atlas, isBackFace, vertices);
        }
    }
    private static void renderQuad(float[] v1, float[] v2, float[] v3, float[] v4, float[] n, float[] c, float u, float v, float[] a, boolean back,
                                   FloatArrayList vertices) {
        if (back) {
            addVertex(v1[0], v1[1], v1[2], n[0], n[1], n[2], c[0], c[1], c[2], 0, 0, a[0], a[1], a[2], a[3], vertices);
            addVertex(v2[0], v2[1], v2[2], n[0], n[1], n[2], c[0], c[1], c[2], u, 0, a[0], a[1], a[2], a[3], vertices);
            addVertex(v3[0], v3[1], v3[2], n[0], n[1], n[2], c[0], c[1], c[2], u, v, a[0], a[1], a[2], a[3], vertices);
            addVertex(v1[0], v1[1], v1[2], n[0], n[1], n[2], c[0], c[1], c[2], 0, 0, a[0], a[1], a[2], a[3], vertices);
            addVertex(v3[0], v3[1], v3[2], n[0], n[1], n[2], c[0], c[1], c[2], u, v, a[0], a[1], a[2], a[3], vertices);
            addVertex(v4[0], v4[1], v4[2], n[0], n[1], n[2], c[0], c[1], c[2], 0, v, a[0], a[1], a[2], a[3], vertices);
        } else {
            addVertex(v1[0], v1[1], v1[2], n[0], n[1], n[2], c[0], c[1], c[2], 0, 0, a[0], a[1], a[2], a[3], vertices);
            addVertex(v4[0], v4[1], v4[2], n[0], n[1], n[2], c[0], c[1], c[2], 0, v, a[0], a[1], a[2], a[3], vertices);
            addVertex(v3[0], v3[1], v3[2], n[0], n[1], n[2], c[0], c[1], c[2], u, v, a[0], a[1], a[2], a[3], vertices);
            addVertex(v1[0], v1[1], v1[2], n[0], n[1], n[2], c[0], c[1], c[2], 0, 0, a[0], a[1], a[2], a[3], vertices);
            addVertex(v3[0], v3[1], v3[2], n[0], n[1], n[2], c[0], c[1], c[2], u, v, a[0], a[1], a[2], a[3], vertices);
            addVertex(v2[0], v2[1], v2[2], n[0], n[1], n[2], c[0], c[1], c[2], u, 0, a[0], a[1], a[2], a[3], vertices);
        }
    }

    private static float[] getUVs(int id) {
        float uMin = (id % (int) Consts.atlasWidthTiles) * Consts.stepU;
        float vMin = (id / (int) Consts.atlasWidthTiles) * Consts.stepV;
        return new float[]{uMin, vMin, uMin + Consts.stepU, vMin + Consts.stepV};
    }

    private static void addVertex(float px, float py, float pz, float nx, float ny, float nz,
                           float r, float g, float b, float u, float v,
                           float uMin, float vMin, float uMax, float vMax, FloatArrayList vertices) {
        vertices.add(px);   vertices.add(py); vertices.add(pz);
        vertices.add(nx);   vertices.add(ny); vertices.add(nz);
        vertices.add(r);    vertices.add(g);  vertices.add(b);
        vertices.add(u);    vertices.add(v);
        vertices.add(uMin); vertices.add(vMin);
        vertices.add(uMax); vertices.add(vMax);
    }

    private static boolean isTransparent(short blockID) {
        if (blockID == 0) return true;
        // LOGIC FOR GRASS, TALL GRASS, etc...
        return false;
    }

    private static short getBlockID(int x, int y, int z, short[] blocks) {
        if (x < 0 || x >= Consts.SIZE || y < 0 || y >= Consts.SIZE || z < 0 || z >= Consts.SIZE) return 0;
        return blocks[x + Consts.SIZE * (y + Consts.SIZE * z)];
    }
    private static BlockRegistry getBlock(int x, int y, int z, short[] blocks) { return BlockRegistries.getBlock(getBlockID(x, y, z, blocks)); }
}
