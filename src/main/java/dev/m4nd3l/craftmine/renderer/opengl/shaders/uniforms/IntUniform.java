package dev.m4nd3l.craftmine.renderer.opengl.shaders.uniforms;

import dev.m4nd3l.craftmine.renderer.opengl.shaders.Uniform;

import static org.lwjgl.opengl.GL20.glUniform1i;

public class IntUniform extends Uniform<Integer> {

    public IntUniform(String uniformName, int shaderID, int value) {
        super(uniformName, shaderID, value);
    }

    @Override
    public void upload() {
        glUniform1i(getLocation(), getValue());
    }
}
