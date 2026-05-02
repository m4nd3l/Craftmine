package dev.m4nd3l.craftmine.multithreading;

import dev.m4nd3l.craftmine.util.Mix;

import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WorldWorkerThread<T> {
    private BlockingQueue<Mix<T, Runnable>> queue;
    private Set<T> activeTasks;
    private Queue<T> ready;
    private Thread workerThread;
    private volatile boolean running = true;

    public WorldWorkerThread(String name) {
        queue = new LinkedBlockingQueue<>();
        activeTasks = ConcurrentHashMap.newKeySet();
        ready = new ConcurrentLinkedQueue<>();
        workerThread = new Thread(() -> {
            while (running) {
                try {
                    var mix = queue.take();
                    var task = mix.getV2();
                    if (task == null) continue;
                    task.run();
                    ready.add(mix.getV1());
                } catch (InterruptedException e) { break; }
            }
        }, name);
        workerThread.start();
    }

    public WorldWorkerThread(float i, boolean randomize) { this("Worker" + i * (randomize ? Math.random() : 1)); }
    public WorldWorkerThread() { this("Worker" + Math.random() * Math.random()); }

    public T addToQueue(T value, Runnable action) {
        if (queue.contains(value)) return value;
        queue.add(new Mix<>(value, action));
        return value;
    }

    public T pollReady() { return ready.poll(); }

    public List<T> pollAll() {
        if (ready.isEmpty()) return List.of();
        var all = ready.stream().toList();
        ready.clear();
        return all;
    }

    public void delete() {
        running = false;

        workerThread.interrupt();
        queue.clear();
        activeTasks.clear();
    }

}
