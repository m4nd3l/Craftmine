/*
 * MIT License
 *
 * Copyright (c) 2026 2026 M4nd3l
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.m4nd3l.craftmine.renderer.renderers;

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