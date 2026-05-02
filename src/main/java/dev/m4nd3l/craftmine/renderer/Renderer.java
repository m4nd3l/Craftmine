package dev.m4nd3l.craftmine.renderer;

import dev.m4nd3l.craftmine.renderer.opengl.VAO;
import dev.m4nd3l.craftmine.renderer.opengl.VBO;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

import java.nio.FloatBuffer;

public abstract class Renderer {
    protected transient FloatBuffer uploadBuffer;

    protected transient VAO vao;
    protected transient VBO vbo;
    protected transient FloatArrayList vertices;
    protected transient int verticesCount;

    public Renderer() {
        vao = new VAO();
        vbo = new VBO();
        vertices = new FloatArrayList();
        verticesCount = 0;
    }

    public Renderer(float[] vertices) {
        this();
        this.vertices = new FloatArrayList(vertices);
        this.verticesCount = vertices.length / 15;
        vbo.uploadData(vertices);
    }

    public abstract void uploadToGPU();
    public abstract void render();

    public void delete() {
        if (uploadBuffer != null) uploadBuffer.clear();
        if (vertices != null) vertices.clear();
        if (vao != null) vao.delete();
        if (vbo != null) vbo.delete();
    }
}
