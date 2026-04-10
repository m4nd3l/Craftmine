package dev.m4nd3l.craftmine.coordinates;

import java.util.Objects;
import java.util.function.BinaryOperator;

public class Coordinates<T> {
    private T x, y, z;

    public Coordinates(T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Coordinates() {}

    public T getX() { return x; }
    public T getY() { return y; }
    public T getZ() { return z; }

    public Coordinates<T> setX(T x) { this.x = x; return this; }
    public Coordinates<T> setY(T y) { this.y = y; return this; }
    public Coordinates<T> setZ(T z) { this.z = z; return this; }

    public Coordinates<T> operation(T x, T y, T z, BinaryOperator<T> operation) {
        this.x = operation.apply(this.x, x);
        this.y = operation.apply(this.y, y);
        this.z = operation.apply(this.z, z);
        return this;
    }

    public Coordinates<T> operation(Coordinates<T> other, BinaryOperator<T> operation) {
        return operation(other.getX(), other.getY(), other.getZ(), operation);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinates c) return getX().equals(c.getX()) && getY().equals(c.getY()) && getZ().equals(c.getZ());
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(x, y, z); }
}
