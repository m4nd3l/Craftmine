package dev.m4nd3l.craftmine.ui.design;

public class Flip {
    private boolean xAxis;
    private boolean yAxis;

    public Flip (boolean all) { this(all, all); }
    public Flip(boolean xAxis, boolean yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public boolean isXAxis() { return xAxis; }
    public boolean isYAxis() { return yAxis; }

    public Flip setXAxis(boolean xAxis) { this.xAxis = xAxis; return this; }
    public Flip setYAxis(boolean yAxis) { this.yAxis = yAxis; return this; }
}
