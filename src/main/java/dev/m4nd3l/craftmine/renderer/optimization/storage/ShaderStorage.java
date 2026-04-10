package dev.m4nd3l.craftmine.renderer.optimization.storage;

import dev.m4nd3l.craftmine.renderer.opengl.ShaderProgram;
import dev.m4nd3l.craftmine.renderer.opengl.shaders.ShaderFiles;
import dev.m4nd3l.craftmine.renderer.optimization.Storage;
import dev.m4nd3l.craftmine.renderer.util.MFile;

public class ShaderStorage extends Storage<ShaderFiles, ShaderProgram> {
    public ShaderProgram getOrCreate(MFile vertex, MFile fragment) {
        ShaderFiles key = new ShaderFiles(vertex, fragment);
        return storage.computeIfAbsent(key, k -> new ShaderProgram(key));
    }

    public ShaderProgram getShaderByID(int shaderID) {
        return storage.values().stream()
                .filter(s -> s.getShaderID() == shaderID)
                .findFirst().orElse(null);
    }
}
