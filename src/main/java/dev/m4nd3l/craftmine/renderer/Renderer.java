package dev.m4nd3l.craftmine.renderer;

import dev.m4nd3l.craftmine.renderer.opengl.VAO;
import dev.m4nd3l.craftmine.renderer.opengl.VBO;
import it.unimi.dsi.fastutil.floats.FloatArrayList;

public abstract class Renderer {
    protected transient VAO vao;
    protected transient VBO vbo;
    protected transient FloatArrayList vertices;
    protected transient int verticesCount;

    public Renderer() {}

    public Renderer(float[] vertices) {
        this.vertices = new FloatArrayList(vertices);
        this.verticesCount = vertices.length / 15;
    }

    public abstract void uploadToGPU();
    public abstract void render();

    public void delete() {
        if (vertices != null) vertices.clear();
        if (vao != null) vao.delete();
        if (vbo != null) vbo.delete();
    }
}
