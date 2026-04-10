package dev.m4nd3l.craftmine.renderer.opengl.shaders;

import dev.m4nd3l.craftmine.renderer.util.MFile;

public class ShaderFiles {
    private MFile vertexGLSL, fragmentGLSL;

    public ShaderFiles(MFile vertexGLSL, MFile fragmentGLSL) {
        this.vertexGLSL = vertexGLSL;
        this.fragmentGLSL = fragmentGLSL;
    }

    public String getVertexSourceCode() { return vertexGLSL.readString(); }
    public String getFragmentSourceCode() { return fragmentGLSL.readString(); }
}
