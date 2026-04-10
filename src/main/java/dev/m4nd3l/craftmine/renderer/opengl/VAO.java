package dev.m4nd3l.craftmine.renderer.opengl;

import static org.lwjgl.opengl.GL30.*;

public class VAO {
    private int vaoID;

    public VAO() {
        vaoID = glGenVertexArrays();
    }

    public void linkAttributes(VBO vbo, int layout, int nComponents, int type, boolean normalized, int size, long pointer) {
        bind();
        vbo.bind();
        glVertexAttribPointer(layout, nComponents, type, normalized, size, pointer);
        glEnableVertexAttribArray(layout);
        vbo.unbind();
        unbind();
    }

    public void bind() {
        glBindVertexArray(vaoID);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(vaoID);
    }
}