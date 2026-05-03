/*
 * MIT License
 *
 * Copyright (c) 2026 2026 M4nd3l
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.m4nd3l.craftmine.renderer.renderers;

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