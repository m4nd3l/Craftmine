package dev.m4nd3l.craftmine.renderer.opengl;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class VBO {
    private int vboID;

    public VBO() { vboID = glGenBuffers(); }

    public void uploadData(float[] data) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }

    public void uploadData(FloatBuffer buffer) {
        bind();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
    }

    public void bind() { glBindBuffer(GL_ARRAY_BUFFER, vboID); }
    public void unbind() { glBindBuffer(GL_ARRAY_BUFFER, 0); }
    public void delete() { glDeleteBuffers(vboID); }
}