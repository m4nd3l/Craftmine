package dev.m4nd3l.craftmine.util;

public class Mix<V1, V2> {
    private V1 v1;
    private V2 v2;

    public Mix(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 getV1() { return v1; }
    public Mix setV1(V1 v1) { this.v1 = v1; return this; }

    public V2 getV2() { return v2; }
    public Mix setV2(V2 v2) { this.v2 = v2; return this; }
}
