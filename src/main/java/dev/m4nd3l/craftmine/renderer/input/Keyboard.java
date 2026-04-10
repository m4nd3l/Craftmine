package dev.m4nd3l.craftmine.renderer.input;

import org.lwjgl.glfw.GLFW;

public class Keyboard {

    private final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST];
    private final boolean[] keysLastFrame = new boolean[GLFW.GLFW_KEY_LAST];

    private int currentModifiers;

    public void init(long window) {
        GLFW.glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = action != GLFW.GLFW_RELEASE;
                this.currentModifiers = mods;
            }
        });
    }

    public void update() {
        System.arraycopy(keys, 0, keysLastFrame, 0, keys.length);
    }

    public boolean isKeyUp(int key) {
        return !isKeyDown(key);
    }
    public boolean isKeyUp(KeyboardKeys key) {
        return !isKeyDown(key);
    }
    public boolean isKeyDown(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }
    public boolean isKeyDown(KeyboardKeys key) {
        return key.getKeyCode() >= 0 && key.getKeyCode() < keys.length && keys[key.getKeyCode()];
    }

    public boolean isKeyPressed(int key) {
        return key >= 0 && key < keys.length && keys[key] && !keysLastFrame[key];
    }
    public boolean isKeyPressed(KeyboardKeys key) {
        int code = key.getKeyCode();
        return code >= 0 && code < keys.length && keys[code] && !keysLastFrame[code];
    }

    public boolean isKeyReleased(int key) {
        return key >= 0 && key < keys.length && !keys[key] && keysLastFrame[key];
    }
    public boolean isKeyReleased(KeyboardKeys key) {
        int code = key.getKeyCode();
        return code >= 0 && code < keys.length && !keys[code] && keysLastFrame[code];
    }

    public boolean isModDown(int modBit) {
        return (currentModifiers & modBit) != 0;
    }

    public boolean isShiftDown() {
        return isModDown(GLFW.GLFW_MOD_SHIFT);
    }
    public boolean isControlDown() {
        return isModDown(GLFW.GLFW_MOD_CONTROL);
    }
    public boolean isAltDown() {
        return isModDown(GLFW.GLFW_MOD_ALT);
    }
}
