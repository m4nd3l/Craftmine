package dev.m4nd3l.craftmine.renderer.world;

import dev.m4nd3l.craftmine.entities.Hitbox;
import dev.m4nd3l.craftmine.renderer.Camera;
import dev.m4nd3l.craftmine.renderer.Renderer;
import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.VAO;
import dev.m4nd3l.craftmine.renderer.opengl.VBO;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.ShaderFiles;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;

public class HitboxRenderer extends Renderer {
    private List<Hitbox> hitboxes;
    private ShaderProgram shader;
    private Camera camera;
    private boolean render = false;

    public HitboxRenderer(List<Hitbox> hitboxes, Camera camera) {
        super();
        this.shader = new ShaderProgram(new ShaderFiles(
            new MFile("assets", "shaders", "hitboxes.vert"),
            new MFile("assets", "shaders", "hitboxes.frag")
        ));
        this.camera = camera;
        this.hitboxes = new ArrayList<>();
        this.hitboxes.addAll(hitboxes);
        hitboxes.forEach(hitbox -> { for (double f : hitbox.generateVertices()) vertices.add((float) f); });
        uploadToGPU();
    }
    public HitboxRenderer(Camera camera) { this(new ArrayList<>(), camera); }
    public HitboxRenderer(Camera camera, Hitbox... hitboxesList) { this(Arrays.stream(hitboxesList).toList(), camera); }

    public void add(Hitbox... hitboxes) {
        this.hitboxes.addAll(Arrays.stream(hitboxes).toList());
        remesh();
    }

    public void remesh() {
        vertices.clear();
        hitboxes.forEach(hitbox -> { for (double f : hitbox.generateVertices()) vertices.add((float) f); });
        uploadToGPU();
    }

    @Override
    public void uploadToGPU() {
        if (vertices == null || vertices.isEmpty())
            hitboxes.forEach(hitbox -> { for (double f : hitbox.generateVertices()) vertices.add((float) f); });

        int size = vertices.size();
        verticesCount = size / 3;

        if (vao == null) vao = new VAO();
        if (vbo == null) vbo = new VBO();

        vao.bind();
        vbo.bind();

        vbo.uploadData(vertices.elements());
        vao.linkAttributes(vbo, 0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        vao.unbind();
        vbo.unbind();
    }

    @Override
    public void render() {
        if (!render) return;
        shader.bind();
        vao.bind();
        camera.uploadUniforms(shader);
        GL11.glDrawArrays(GL11.GL_LINES, 0, verticesCount);
        vao.unbind();
        shader.unbind();
    }

    public void swap() { render = !render; }
}