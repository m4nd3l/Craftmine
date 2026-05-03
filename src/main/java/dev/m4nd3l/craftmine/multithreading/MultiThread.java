package dev.m4nd3l.craftmine.multithreading;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MultiThread<T> {
    private List<WorldWorkerThread<T>> threadList;
    private Queue<T> ready;
    private int threads, worker;

    public MultiThread(int threads) {
        this.threads = threads;
        this.threadList = new ArrayList<>(threads);
        this.ready = new ConcurrentLinkedQueue<>();
        this.worker = 0;
        for (int i = 0; i < threads; i++) threadList.add(i, new WorldWorkerThread<>(i, true));
    }

    public void addThread() { addThread(new WorldWorkerThread<>(threads - 1, true)); }
    public void addThread(WorldWorkerThread<T> thread) {
        this.threads++;
        threadList.add(threads - 1, thread);
    }


    public void addToQueue(T value, Runnable runnable) {
        getCurrent().addToQueue(value, runnable);
        var polled = getCurrent().pollReady();
        if (polled != null) ready.add(polled);
        increment();
    }

    public void update() {
        if (worker == 0) threadList.forEach((thread) -> ready.addAll(thread.pollAll()));
    }

    public T pollReady() { var polled = getCurrent().pollReady(); if (polled != null) ready.add(polled); return ready.poll(); }

    public void delete() { threadList.forEach(WorldWorkerThread::delete); }

    private WorldWorkerThread<T> getCurrent() { return threadList.get(worker); }

    private void increment() {
        worker++;
        if (worker >= threads) worker = 0;
    }
}
