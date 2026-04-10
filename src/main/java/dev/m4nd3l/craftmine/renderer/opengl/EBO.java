package dev.m4nd3l.craftmine.renderer.opengl;

import static org.lwjgl.opengl.GL15.*;

public class EBO {
    private int eboID;

    public EBO(int[] indices) {
        eboID = glGenBuffers();
        bind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public void bind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    }

    public void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void delete() {
        glDeleteBuffers(eboID);
    }

}
