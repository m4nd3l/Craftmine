package dev.m4nd3l.craftmine.ui.layout;

import org.joml.Vector2f;

public class Dimensions {
    private float xPercent, yPercent;
    private Vector2f size = new Vector2f();
    private Vector2f parentSizeRef;

    public Dimensions(float xPercent, float yPercent, Vector2f parentSize) {
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.parentSizeRef = parentSize;
        recalculateSize((int) parentSize.x, (int) parentSize.y);
    }

    public void recalculateSize(int width, int height) {
        size.x = width * (xPercent / 100f);
        size.y = height * (yPercent / 100f);

        if (parentSizeRef != null) {
            parentSizeRef.set(width, height);
        }
    }

    public Vector2f getSize() { return size; }
    public Vector2f getParentSize() { return parentSizeRef; }
}