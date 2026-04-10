package dev.m4nd3l.craftmine.renderer;

import dev.m4nd3l.craftmine.coordinates.CoordinatesConverter;
import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.registries.BlockRegistries;
import dev.m4nd3l.craftmine.registries.registry.BlockRegistry;
import dev.m4nd3l.craftmine.renderer.opengl.VAO;
import dev.m4nd3l.craftmine.renderer.opengl.VBO;
import dev.m4nd3l.craftmine.renderer.optimization.RenderingOptimization;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

import static org.lwjgl.opengl.GL11.*;

public class SubChunkRenderer {
    private transient int SIZE;
    private SubChunkCoordinates subChunkCoordinates;

    private transient float stepU, stepV;
    private transient float atlasWidthTiles;

    private transient VAO vao;
    private transient VBO vbo;
    private transient FloatArrayList vertices;
    private transient int verticesCount;

    private transient short[] tempBlocks;
    private transient boolean isDirty;

    public SubChunkRenderer initialize(int size, SubChunkCoordinates subChunkCoordinates) {
        this.SIZE = size;
        if (subChunkCoordinates != null) this.subChunkCoordinates = subChunkCoordinates;
        this.vertices = new FloatArrayList();

        var texture = RenderingOptimization.textures.getOrCreate(new MFile("assets", "textures", "blockAtlas.png"));
        this.atlasWidthTiles = texture.getAtlasXSize();
        this.stepU = 1.0f / atlasWidthTiles;
        this.stepV = 1.0f / texture.getAtlasYSize();
        dirty();
        return this;
    }

    public void generateMesh(short[] blocks) {
        this.tempBlocks = blocks;
        this.vertices = new FloatArrayList();

        for (int axis = 0; axis < 3; axis++) runGreedyMesh(axis);

        uploadToGPU();
        this.isDirty = false;
        this.tempBlocks = null;
    }

    private void runGreedyMesh(int d) {
        int u = (d + 1) % 3;
        int v = (d + 2) % 3;
        int[] x = new int[3];
        int[] q = new int[3]; q[d] = 1;

        short[] mask = new short[SIZE * SIZE];

        for (x[d] = -1; x[d] < SIZE; ) {
            int n = 0;
            for (x[v] = 0; x[v] < SIZE; x[v]++) {
                for (x[u] = 0; x[u] < SIZE; x[u]++) {
                    short b1 = (x[d] >= 0) ? getBlockID(x[0], x[1], x[2]) : 0;
                    short b2 = (x[d] < SIZE - 1) ? getBlockID(x[0] + q[0], x[1] + q[1], x[2] + q[2]) : 0;

                    mask[n++] = (b1 != 0 && b2 != 0 && b1 == b2) ? 0 : (b1 != 0 ? b1 : (short)-b2);
                }
            }

            x[d]++;
            n = 0;

            for (int j = 0; j < SIZE; j++) {
                for (int i = 0; i < SIZE; ) {
                    if (mask[n] != 0) {
                        short currentID = mask[n];
                        int width, height;

                        for (width = 1; i + width < SIZE && mask[n + width] == currentID; width++);

                        boolean done = false;
                        for (height = 1; j + height < SIZE; height++) {
                            for (int k = 0; k < width; k++) {
                                if (mask[n + k + height * SIZE] != currentID) { done = true; break; }
                            }
                            if (done) break;
                        }

                        x[u] = i; x[v] = j;
                        int[] du = new int[3]; du[u] = width;
                        int[] dv = new int[3]; dv[v] = height;

                        addGreedyQuad(x, du, dv, currentID, d, width, height);

                        for (int l = 0; l < height; l++) {
                            for (int k = 0; k < width; k++) mask[n + k + l * SIZE] = 0;
                        }
                        i += width; n += width;
                    } else {
                        i++; n++;
                    }
                }
            }
        }
    }

    private void addGreedyQuad(int[] x, int[] du, int[] dv, short blockID, int axis, int width, int height) {
        final boolean isBackFace = blockID < 0;
        final BlockRegistry block = BlockRegistries.getBlock((short) Math.abs(blockID));
        final var worldOffset = CoordinatesConverter.toBlock(subChunkCoordinates);

        float x0 = x[0] + worldOffset.getX(), y0 = x[1] + worldOffset.getY(), z0 = x[2] + worldOffset.getZ();

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
            renderFinalQuad(p1, p2, p3, p4, norm, col, uMax, vMax, atlas, isBackFace);
        } else if (axis == 0) { // X-SIDES
            uMax = height; // Z
            vMax = width;  // Y
            renderFinalQuad(p4, p1, p2, p3, norm, col, uMax, vMax, atlas, isBackFace);
        } else { // Z-SIDES
            uMax = width;  // X
            vMax = height; // Y
            renderFinalQuad(p4, p3, p2, p1, norm, col, uMax, vMax, atlas, isBackFace);
        }
    }

    private void renderFinalQuad(float[] v1, float[] v2, float[] v3, float[] v4, float[] n, float[] c, float u, float v, float[] a, boolean back) {
        if (back) {
            addVertex(v1, n, c, 0, 0, a);
            addVertex(v2, n, c, u, 0, a);
            addVertex(v3, n, c, u, v, a);
            addVertex(v1, n, c, 0, 0, a);
            addVertex(v3, n, c, u, v, a);
            addVertex(v4, n, c, 0, v, a);
        } else {
            addVertex(v1, n, c, 0, 0, a);
            addVertex(v4, n, c, 0, v, a);
            addVertex(v3, n, c, u, v, a);
            addVertex(v1, n, c, 0, 0, a);
            addVertex(v3, n, c, u, v, a);
            addVertex(v2, n, c, u, 0, a);
        }
    }

    private void addVertex(float[] pos, float[] norm, float[] col, float u, float v, float[] atlas) {
        vertices.add(pos[0]);  vertices.add(pos[1]);  vertices.add(pos[2]);
        vertices.add(norm[0]); vertices.add(norm[1]); vertices.add(norm[2]);
        vertices.add(col[0]);  vertices.add(col[1]);  vertices.add(col[2]);
        vertices.add(u);       vertices.add(v);
        vertices.add(atlas[0]); vertices.add(atlas[1]);
        vertices.add(atlas[2]); vertices.add(atlas[3]);
    }

    private void uploadToGPU() {
        float[] data = vertices.toFloatArray();
        verticesCount = data.length / 15;

        if (vao == null) vao = new VAO();
        if (vbo == null) vbo = new VBO();
        vbo.uploadData(data);

        int stride = 15 * 4;
        vao.linkAttributes(vbo, 0, 3, GL_FLOAT, false, stride, 0);      // Position
        vao.linkAttributes(vbo, 1, 3, GL_FLOAT, false, stride, 12);     // Normal
        vao.linkAttributes(vbo, 2, 3, GL_FLOAT, false, stride, 24);     // Color
        vao.linkAttributes(vbo, 3, 2, GL_FLOAT, false, stride, 36);     // UV Tiling (Passes width/height)
        vao.linkAttributes(vbo, 4, 2, GL_FLOAT, false, stride, 44);     // Atlas Min
        vao.linkAttributes(vbo, 5, 2, GL_FLOAT, false, stride, 52);     // Atlas Max
    }

    private float[] getUVs(int id) {
        float uMin = (id % (int)atlasWidthTiles) * stepU;
        float vMin = (id / (int)atlasWidthTiles) * stepV;
        return new float[]{uMin, vMin, uMin + stepU, vMin + stepV};
    }

    public void render() {
        if (verticesCount == 0) return;
        vao.bind();
        glDrawArrays(GL_TRIANGLES, 0, verticesCount);
        vao.unbind();
    }

    public void delete() {
        if (vao != null) vao.delete();
        if (vbo != null) vbo.delete();
    }

    private short getBlockID(int x, int y, int z) {
        return tempBlocks[x + SIZE * (y + SIZE * z)];
    }

    public boolean isDirty() { return isDirty; }
    public SubChunkRenderer dirty() { isDirty = true; return this; }
}