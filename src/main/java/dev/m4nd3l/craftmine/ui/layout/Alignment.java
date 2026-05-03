package dev.m4nd3l.craftmine.ui.layout;

import org.joml.Vector2f;

public enum Alignment {
    TOP_LEFT(0.0f, 0.0f),
    TOP_CENTER(0.5f, 0.0f),
    TOP_RIGHT(1.0f, 0.0f),

    CENTER_LEFT(0.0f, 0.5f),
    CENTER(0.5f, 0.5f),
    CENTER_RIGHT(1.0f, 0.5f),

    BOTTOM_LEFT(0.0f, 1.0f),
    BOTTOM_CENTER(0.5f, 1.0f),
    BOTTOM_RIGHT(1.0f, 1.0f);

    private float xRatio, yRatio;

    Alignment(float ratioX, float ratioY) {
        this.xRatio = ratioX;
        this.yRatio = ratioY;
    }

    public float getXRatio() { return xRatio; }
    public float getYRatio() { return yRatio; }

    public Vector2f getOffset(Dimensions dimensions) {
        return getOffset(dimensions, new Margin(0));
    }

    public Vector2f getOffset(Dimensions dimensions, Margin margin) {
        Vector2f parentSize = dimensions.getParentSize();
        Vector2f compSize = dimensions.getSize();

        float x = (parentSize.x * xRatio) - (compSize.x * xRatio) + margin.getLeftMargin();
        float y = (parentSize.y * yRatio) - (compSize.y * yRatio) + margin.getTopMargin();

        return new Vector2f(x, y);
    }
}