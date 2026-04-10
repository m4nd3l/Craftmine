package dev.m4nd3l.craftmine.blocks.settings;

public class Color {
    private float r, g, b;

    public Color(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public float getR() { return r; }
    public float getG() { return g; }
    public float getB() { return b; }

    public Color setR(float r) { this.r = r; return this; }
    public Color setG(float g) { this.g = g; return this; }
    public Color setB(float b) { this.b = b; return this; }
}
