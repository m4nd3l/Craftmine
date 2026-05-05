package dev.m4nd3l.craftmine.ui.design;

import org.joml.Vector4f;

public class UIColor {
    private Vector4f color;

    public UIColor(Vector4f color) { this.color = color; }
    public UIColor(float r, float g, float b, float a) { this(new Vector4f(r, g, b, a)); }
    public UIColor(float r, float g, float b) { this(new Vector4f(r, g, b, 1.0f)); }
    public UIColor(UIColor color) { this(color.getColor()); }
    public UIColor(UIColors color) { this(color.getColor()); }

    public Vector4f getColor() { return color; }
    public UIColor setColor(Vector4f color) { this.color = color; return this; }
}
