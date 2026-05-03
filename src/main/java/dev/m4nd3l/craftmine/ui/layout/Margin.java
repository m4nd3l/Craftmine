package dev.m4nd3l.craftmine.ui.layout;

public class Margin {
    private float leftMargin, topMargin;

    public Margin() { this(0); }
    public Margin(float all) { this(all, all); }
    public Margin(float left, float top) {
        this.leftMargin = left;
        this.topMargin = top;
    }

    public float getLeftMargin() { return leftMargin; }
    public float getTopMargin() { return topMargin; }

    public Margin setLeftMargin(float leftMargin) { this.leftMargin = leftMargin; return this; }
    public Margin setTopMargin(float topMargin) { this.topMargin = topMargin; return this; }
}