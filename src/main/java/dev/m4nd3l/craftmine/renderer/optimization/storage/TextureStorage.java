package dev.m4nd3l.craftmine.renderer.optimization.storage;

import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.optimization.Storage;
import dev.m4nd3l.craftmine.renderer.util.MFile;

public class TextureStorage extends Storage<MFile, Texture> {
    public Texture getOrCreate(MFile texture) {
        return storage.computeIfAbsent(texture, k -> new Texture(texture));
    }
}
