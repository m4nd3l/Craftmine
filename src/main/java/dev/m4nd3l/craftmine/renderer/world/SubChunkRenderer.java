package dev.m4nd3l.craftmine.renderer.world;

import dev.m4nd3l.craftmine.coordinates.SubChunkCoordinates;
import dev.m4nd3l.craftmine.renderer.opengl.VAO;
import dev.m4nd3l.craftmine.renderer.opengl.VBO;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

import static org.lwjgl.opengl.GL11.*;

public class SubChunkRenderer {
    private transient VAO vao;
    private transient VBO vbo;
    private transient FloatArrayList vertices;
    private transient int verticesCount;

    private transient boolean isDirty;

    public SubChunkRenderer initialize() { this.vertices = new FloatArrayList(); dirty(); return this; }

    public void uploadToGPU() {
        if (vertices == null) return;
        float[] data = vertices.toFloatArray();
        verticesCount = data.length / 15;

        if (vao == null) vao = new VAO();
        if (vbo == null) vbo = new VBO();
        vbo.uploadData(data);

        int stride = 15 * 4;
        vao.linkAttributes(vbo, 0, 3, GL_FLOAT, false, stride, 0);  // Position
        vao.linkAttributes(vbo, 1, 3, GL_FLOAT, false, stride, 12); // Normal
        vao.linkAttributes(vbo, 2, 3, GL_FLOAT, false, stride, 24); // Color
        vao.linkAttributes(vbo, 3, 2, GL_FLOAT, false, stride, 36); // UV Tiling (Passes width/height)
        vao.linkAttributes(vbo, 4, 2, GL_FLOAT, false, stride, 44); // Atlas Min
        vao.linkAttributes(vbo, 5, 2, GL_FLOAT, false, stride, 52); // Atlas Max
        isDirty = false;
    }

    public void setVertices(FloatArrayList vertices) { this.vertices = vertices; }

    public void render() { if (verticesCount == 0) return; vao.bind(); glDrawArrays(GL_TRIANGLES, 0, verticesCount); vao.unbind(); }
    public void delete() { if (vao != null) vao.delete(); if (vbo != null) vbo.delete(); }

    public boolean isDirty() { return isDirty; }
    public SubChunkRenderer dirty() { isDirty = true; return this; }
    public void undirty() { isDirty = false; }
}