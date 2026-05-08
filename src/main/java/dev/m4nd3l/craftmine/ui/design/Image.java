package dev.m4nd3l.craftmine.ui.design;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import org.joml.Vector2f;

public class Image {
    private UIColor color;
    private Rotation rotation;
    private Flip flip;
    private Texture texture;

    public Image() { color = new UIColor(1, 1, 1, 1); }

    public Image setRotation(Rotation rotation) { this.rotation = rotation; Main.craftmine.UIManager.pushRendering(); return this; }
    public Image setFlip(Flip flip) { this.flip = flip; Main.craftmine.UIManager.pushRendering(); return this; }
    public Image setColor(UIColors color) { return setColor(new UIColor(color)); }
    public Image setColor(UIColor color) { this.color = color; Main.craftmine.UIManager.pushRendering(); return this; }
    public Image setTexture(Texture texture) { return setTexture(texture, true); }
    public Image setTexture(Texture texture, boolean deleteOld) {
        if (deleteOld && this.texture != null) this.texture.delete();
        this.texture = texture;
        Main.craftmine.UIManager.pushRendering();
        return this;
    }

    public void pushRendering(UIRenderer renderer, Vector2f absolutePosition, Vector2f size) {
        float u0 = 0, v0 = 0; // Top-Left
        float u1 = 1, v1 = 0; // Top-Right
        float u2 = 1, v2 = 1; // Bottom-Right
        float u3 = 0, v3 = 1; // Bottom-Left

        // FLIP
        if (flip != null && flip.isXAxis()) {
            float tempV0 = v0, tempV1 = v1;
            v0 = v2;
            v1 = v3;
            v2 = tempV0;
            v3 = tempV1;
        }
        if (flip != null && flip.isYAxis()) {
            float tempU0 = u0, tempU3 = u3;
            u0 = u1;
            u3 = u2;
            u1 = tempU0;
            u2 = tempU3;
        }

        // ROTATION
        float finalU0 = u0, finalV0 = v0,
              finalU1 = u1, finalV1 = v1,
              finalU2 = u2, finalV2 = v2,
              finalU3 = u3, finalV3 = v3;

        if (rotation != null)
            switch (rotation.getValue()) {
                case D_90, D_M270 -> {
                    // 90° CW or -270° CCW
                    finalU0 = u3; finalV0 = v3;
                    finalU1 = u0; finalV1 = v0;
                    finalU2 = u1; finalV2 = v1;
                    finalU3 = u2; finalV3 = v2;
                }
                case D_180, D_M180 -> {
                    // 180°
                    finalU0 = u2; finalV0 = v2;
                    finalU1 = u3; finalV1 = v3;
                    finalU2 = u0; finalV2 = v0;
                    finalU3 = u1; finalV3 = v1;
                }
                case D_270, D_M90 -> {
                    // 270° CW or -90° CCW
                    finalU0 = u1; finalV0 = v1;
                    finalU1 = u2; finalV1 = v2;
                    finalU2 = u3; finalV2 = v3;
                    finalU3 = u0; finalV3 = v0;
                }
                default -> { break; }
            }

        // RENDER
        renderer.addRotatedRect(
                absolutePosition.x, absolutePosition.y,
                size.x, size.y,
                color, texture,
                finalU0, finalV0, finalU1, finalV1, finalU2, finalV2, finalU3, finalV3
        );
    }

    public Texture getTexture() { return texture; }
}
