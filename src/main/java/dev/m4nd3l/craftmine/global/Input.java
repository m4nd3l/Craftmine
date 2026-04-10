package dev.m4nd3l.craftmine.global;

import dev.m4nd3l.craftmine.renderer.input.*;

public class Input {
    public static Keyboard keyboard;
    public static Mouse mouse;

    static {
        keyboard = new Keyboard();
        mouse = new Mouse();
    }

    public static void initialize(long glfwWindow) {
        keyboard.init(glfwWindow);
        mouse.init(glfwWindow);
    }

    public static void update() { keyboard.update(); }
    public static void endFrame() { mouse.endFrame(); }
}
