package dev.m4nd3l.craftmine;

import dev.m4nd3l.craftmine.renderer.Window;

public class Main {
    public static Craftmine craftmine;
    public static void main(String[] args) {
        craftmine = new Craftmine(true);
        Window window = new Window("CraftMine", 1920, 1080, true, craftmine);
        window.start();
        window.terminate();
    }
}