package dev.m4nd3l.craftmine.renderer.optimization;

import java.util.HashMap;
import java.util.Map;

public class Storage<K, V> {
    protected Map<K, V> storage;

    public Storage() { storage = new HashMap<>(); }

    public V get(K key) { return storage.get(key); }
    public V getOrDefault(K key, V defaul) { return storage.getOrDefault(key, defaul); }

    public Storage<K, V> put(K key, V value) { storage.put(key, value); return this; }
}
