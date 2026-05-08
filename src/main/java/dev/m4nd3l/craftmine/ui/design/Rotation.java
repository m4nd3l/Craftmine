package dev.m4nd3l.craftmine.ui.design;

public class Rotation {
    private RotationValue value;
    public Rotation(float degrees) { this(RotationValue.findNearest(degrees)); }
    public Rotation(RotationValue value) { this.value = value; }

    public RotationValue getValue() { return value; }

    public Rotation setValue(float degrees) { return setValue(RotationValue.findNearest(degrees)); }
    public Rotation setValue(RotationValue value) { this.value = value; return this; }
}
