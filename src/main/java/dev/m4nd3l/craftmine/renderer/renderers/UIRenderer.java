package dev.m4nd3l.craftmine.renderer.renderers;

import dev.m4nd3l.craftmine.renderer.Renderer;
import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.opengl.VAO;
import dev.m4nd3l.craftmine.renderer.opengl.VBO;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.ShaderFiles;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.uniforms.Matrix4fUniform;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.ui.design.UIColor;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;

public class UIRenderer extends Renderer {
    private ShaderProgram shader;
    private Matrix4f projectionMatrix;
    private boolean renderActive;
    private Texture currentTexture;
    private Texture whiteTexture;

    public UIRenderer() {
        this.renderActive = true;
        this.projectionMatrix = new Matrix4f();
        this.shader = new ShaderProgram(new ShaderFiles(
                new MFile("assets", "shaders", "ui.vert"),
                new MFile("assets", "shaders", "ui.frag")
        ));

        this.whiteTexture = createWhitePixel();
        this.currentTexture = null;
    }

    public void updateSize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        projectionMatrix.identity().ortho(0, width, height, 0, -1, 1);
    }

    public void addRect(float x, float y, float w, float h, UIColor color, Texture texture) {
        Texture texToUse = (texture == null) ? whiteTexture : texture;
        if (currentTexture != null && texToUse != currentTexture) flush();
        this.currentTexture = texToUse;

        pushVertex(x, y, color.getColor(), 0, 0);
        pushVertex(x + w, y, color.getColor(), 1, 0);
        pushVertex(x + w, y + h, color.getColor(), 1, 1);

        pushVertex(x, y, color.getColor(), 0, 0);
        pushVertex(x + w, y + h, color.getColor(), 1, 1);
        pushVertex(x, y + h, color.getColor(), 0, 1);
    }

    public static Texture createWhitePixel() {
        ByteBuffer buffer = BufferUtils.createByteBuffer(4);
        buffer.put((byte) 255).put((byte) 255).put((byte) 255).put((byte) 255);
        buffer.flip();

        return new Texture(1, 1, buffer);
    }

    private void pushVertex(float x, float y, Vector4f c, float u, float v) {
        vertices.add(x); vertices.add(y);
        vertices.add(c.x); vertices.add(c.y);
        vertices.add(c.z); vertices.add(c.w);
        vertices.add(u);   vertices.add(v);
    }

    public void flush() {
        if (vertices.isEmpty()) return;

        uploadToGPU();

        shader.bind();
        shader.uploadUniform(new Matrix4fUniform("uProjection", shader.getShaderID(), projectionMatrix));

        if (currentTexture != null) currentTexture.bind();

        vao.bind();
        GL11.glDrawArrays(GL_TRIANGLES, 0, verticesCount);

        vao.unbind();
        vertices.clear();
    }

    @Override
    public void uploadToGPU() {
        if (vertices == null || vertices.isEmpty()) return;

        verticesCount = vertices.size() / 8;

        if (vao == null) vao = new VAO();
        if (vbo == null) vbo = new VBO();

        vao.bind();
        vbo.bind();

        vbo.uploadData(vertices.elements());
        int stride = 8 * Float.BYTES;

        vao.linkAttributes(vbo, 0, 2, GL_FLOAT, false, stride, 0);
        vao.linkAttributes(vbo, 1, 4, GL_FLOAT, false, stride, 2 * Float.BYTES);
        vao.linkAttributes(vbo, 2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);

        vao.unbind();
        vbo.unbind();
    }

    @Override
    public void render() {
        if (vertices == null) return;
        if (!renderActive) return;
        if (!vertices.isEmpty()) flush();
        else if (verticesCount > 0) {
            shader.bind();
            shader.uploadUniform(new Matrix4fUniform("uProjection", shader.getShaderID(), projectionMatrix));
            if (currentTexture != null) currentTexture.bind();
            vao.bind();
            GL11.glDrawArrays(GL_TRIANGLES, 0, verticesCount);
            vao.unbind();
            shader.unbind();
        }
    }

    public void swap() { this.renderActive = !this.renderActive; }

    @Override
    public void delete() {
        super.delete();
        verticesCount = 0;
        shader.delete();
    }
}