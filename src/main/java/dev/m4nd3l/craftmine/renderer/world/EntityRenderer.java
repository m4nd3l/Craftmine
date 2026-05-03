package dev.m4nd3l.craftmine.renderer.world;

import dev.m4nd3l.craftmine.entities.Entity;
import dev.m4nd3l.craftmine.renderer.Camera;
import dev.m4nd3l.craftmine.renderer.Renderer;

import java.util.List;

public class EntityRenderer extends Renderer {

    private HitboxRenderer hitboxRenderer;
    private Camera camera;
    private List<Entity> entities;

    public EntityRenderer(Camera camera) {
        hitboxRenderer = new HitboxRenderer(camera);
        this.camera = camera;
    }

    @Override
    public void uploadToGPU() {

        hitboxRenderer.uploadToGPU();
    }

    @Override
    public void render() {
        // render
        hitboxRenderer.render();
    }

    public void swapHitboxes() {
        hitboxRenderer.swap();
    }

    @Override
    public void delete() {
        super.delete();
        hitboxRenderer.delete();
    }
}