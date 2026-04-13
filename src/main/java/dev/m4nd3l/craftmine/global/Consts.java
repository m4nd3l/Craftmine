package dev.m4nd3l.craftmine.global;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.optimization.RenderingOptimization;
import dev.m4nd3l.craftmine.renderer.util.MFile;

public class Consts {
    public static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static int SIZE = 16;
    public static Texture texture = RenderingOptimization.textures.getOrCreate(new MFile("assets", "textures", "blockAtlas.png"));
    public static float
            atlasWidthTiles = texture.getAtlasXSize(),
            stepU = 1.0f / atlasWidthTiles,
            stepV = 1.0f / texture.getAtlasYSize();
}
