package dev.m4nd3l.craftmine.coordinates;

import java.util.Objects;
public class Coordinates {

    private float x, y, z;

    public Coordinates(int x, int y, int z) { this((float) x, (float) y, (float) z); }
    public Coordinates(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public Coordinates() {}

    public float getX() { return x; }
    public float getY() { return y; }
    public float getZ() { return z; }

    public Coordinates setX(float x) { this.x = x; return this; }
    public Coordinates setY(float y) { this.y = y; return this; }
    public Coordinates setZ(float z) { this.z = z; return this; }

    public Coordinates sum(Coordinates other) {
        this.x += other.getX();
        this.y += other.getY();
        this.z += other.getZ();
        return this;
    }

    public Coordinates subtract(Coordinates other) {
        this.x -= other.getX();
        this.y -= other.getY();
        this.z -= other.getZ();
        return this;
    }

    public Coordinates multiply(Coordinates other) {
        this.x *= other.getX();
        this.y *= other.getY();
        this.z *= other.getZ();
        return this;
    }

    public Coordinates divide(Coordinates other) {
        this.x /= other.getX();
        this.y /= other.getY();
        this.z /= other.getZ();
        return this;
    }

    public Coordinates operation(Coordinates other, Operation operation) {
        this.x = operation.run(this.x, other.getX());
        this.y = operation.run(this.y, other.getY());
        this.z = operation.run(this.z, other.getZ());
        return this;
    }

    @Override
    public String toString() {
        return "COORDINATES: " + x + "; " + y + "; " + z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates c) return Objects.equals(getX(), c.getX()) && Objects.equals(getY(), c.getY()) && Objects.equals(getZ(), c.getZ());
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y, z); }
}

interface Operation {  float run(float thisX, float otherX); }