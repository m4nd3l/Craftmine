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

                        addGreedyQuad(x, du, dv, currentID, d);

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

    private void addGreedyQuad(int[] x, int[] du, int[] dv, short blockID, int axis) {
        final boolean isBackFace = blockID < 0;
        final BlockRegistry block = BlockRegistries.getBlock((short) Math.abs(blockID));
        final var worldOffset = CoordinatesConverter.toBlock(subChunkCoordinates);

        float x0 = x[0] + worldOffset.getX(), y0 = x[1] + worldOffset.getY(), z0 = x[2] + worldOffset.getZ();

        float u1, v1;
        if (axis == 1) {
            u1 = Math.max(Math.max(Math.abs(du[0]), Math.abs(du[1])), Math.abs(du[2]));
            v1 = Math.max(Math.max(Math.abs(dv[0]), Math.abs(dv[1])), Math.abs(dv[2]));
        } else {
            if (du[1] != 0) {
                v1 = Math.abs(du[1]);
                u1 = Math.max(Math.abs(dv[0]), Math.abs(dv[2]));
            } else {
                v1 = Math.abs(dv[1]);
                u1 = Math.max(Math.abs(du[0]), Math.abs(du[2]));
            }
        }

        float nx = (axis == 0) ? (isBackFace ? -1 : 1) : 0;
        float ny = (axis == 1) ? (isBackFace ? -1 : 1) : 0;
        float nz = (axis == 2) ? (isBackFace ? -1 : 1) : 0;

        int texID = (axis == 1) ? (isBackFace ? block.getBottomTextureID() : block.getTopTextureID()) : block.getSideTextureID();
        float[] uvs = getUVs(texID);

        var color = block.getInstance().getSettings().getColor();
        float[] colorFloatArray = new float[3];
        colorFloatArray[0] = (color != null) ? color.getR() : 1.0f;
        colorFloatArray[1] = (color != null) ? color.getG() : 1.0f;
        colorFloatArray[2] = (color != null) ? color.getB() : 1.0f;

        float[][] c = {
                {x0, y0, z0},
                {x0 + du[0], y0 + du[1], z0 + du[2]},
                {x0 + du[0] + dv[0], y0 + du[1] + dv[1], z0 + du[2] + dv[2]},
                {x0 + dv[0], y0 + dv[1], z0 + dv[2]}
        };

        float[] uv0 = {0, 0};
        float[] uv1 = calculateUV(c[1], c[0], axis, u1, v1);
        float[] uv2 = {u1, v1};
        float[] uv3 = calculateUV(c[3], c[0], axis, u1, v1);

        float[] normals = { nx, ny, nz };

        if (isBackFace) {
            addVertex(c[0], normals, colorFloatArray, uv0[0], uv0[1], uvs);
            addVertex(c[1], normals, colorFloatArray, uv1[0], uv1[1], uvs);
            addVertex(c[2], normals, colorFloatArray, uv2[0], uv2[1], uvs);
            addVertex(c[0], normals, colorFloatArray, uv0[0], uv0[1], uvs);
            addVertex(c[2], normals, colorFloatArray, uv2[0], uv2[1], uvs);
            addVertex(c[3], normals, colorFloatArray, uv3[0], uv3[1], uvs);
        } else {
            addVertex(c[0], normals, colorFloatArray, uv0[0], uv0[1], uvs);
            addVertex(c[3], normals, colorFloatArray, uv3[0], uv3[1], uvs);
            addVertex(c[2], normals, colorFloatArray, uv2[0], uv2[1], uvs);
            addVertex(c[0], normals, colorFloatArray, uv0[0], uv0[1], uvs);
            addVertex(c[2], normals, colorFloatArray, uv2[0], uv2[1], uvs);
            addVertex(c[1], normals, colorFloatArray, uv1[0], uv1[1], uvs);
        }
    }

    private void addVertex(float[] position, float[] normals, float[] color, float u, float v, float[] atlas) {
        vertices.add(position[0]); vertices.add(position[1]); vertices.add(position[2]);
        vertices.add(normals[0]);  vertices.add(normals[1]);  vertices.add(normals[2]);
        vertices.add(color[0]);    vertices.add(color[1]);    vertices.add(color[2]);
        vertices.add(u);           vertices.add(v);
        vertices.add(atlas[0]);    vertices.add(atlas[1]);
        vertices.add(atlas[2]);    vertices.add(atlas[3]);
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
        vao.linkAttributes(vbo, 3, 2, GL_FLOAT, false, stride, 36);     // UV Tiling
        vao.linkAttributes(vbo, 4, 2, GL_FLOAT, false, stride, 44);     // Atlas Min
        vao.linkAttributes(vbo, 5, 2, GL_FLOAT, false, stride, 52);     // Atlas Max
    }

    private float[] getUVs(int id) {
        float uMin = (id % (int)atlasWidthTiles) * stepU;
        float vMin = (id / (int)atlasWidthTiles) * stepV;
        return new float[]{uMin, vMin, uMin + stepU, vMin + stepV};
    }
    private float[] calculateUV(float[] current, float[] start, int axis, float uMax, float vMax) {
        if (axis == 1) return new float[] {
                Math.abs(current[0] - start[0]) > 0.001f ? uMax : 0,
                Math.abs(current[2] - start[2]) > 0.001f ? vMax : 0
        };
        else return new float[] {
                Math.abs(current[1] - start[1]) < 0.001f ? uMax : 0,
                Math.abs(current[1] - start[1]) > 0.001f ? vMax : 0
        };

    }

    public void render() { if (verticesCount == 0) return; vao.bind(); glDrawArrays(GL_TRIANGLES, 0, verticesCount); vao.unbind(); }
    public void delete() { if (vao != null) vao.delete(); if (vbo != null) vbo.delete(); }

    private short getBlockID(int x, int y, int z) { return tempBlocks[x + SIZE * (y + SIZE * z)]; }
    public boolean isDirty() { return isDirty; }
    public SubChunkRenderer dirty() { isDirty = true; return this; }

    public SubChunkCoordinates getCoordinates() { return subChunkCoordinates; }
}