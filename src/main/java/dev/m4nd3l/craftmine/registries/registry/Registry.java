package dev.m4nd3l.craftmine.registries.registry;

public class Registry<T> {
    private T instance;
    private int id;

    public Registry(int id, T instance) {
        this.id = id;
        this.instance = instance;
    }

    public int getId() { return id; }

    public T getInstance() { return instance; }
}