package dev.m4nd3l.craftmine.renderer.input;

import static org.lwjgl.glfw.GLFW.*;

public enum MouseKeys {
    LEFT(GLFW_MOUSE_BUTTON_LEFT, "left"),
    RIGHT(GLFW_MOUSE_BUTTON_RIGHT, "right"),
    MIDDLE(GLFW_MOUSE_BUTTON_MIDDLE, "middle"),
    BUTTON_4(GLFW_MOUSE_BUTTON_4, "button 4"),
    BUTTON_5(GLFW_MOUSE_BUTTON_5, "button 5"),
    BUTTON_6(GLFW_MOUSE_BUTTON_6, "button 6"),
    BUTTON_7(GLFW_MOUSE_BUTTON_7, "button 7"),
    BUTTON_8(GLFW_MOUSE_BUTTON_8, "button 8");

    private final int buttonCode;
    private final String name;

    MouseKeys(int buttonCode, String name) {
        this.buttonCode = buttonCode;
        this.name = name;
    }

    public int getButtonCode() {
        return buttonCode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("MouseButton %s: %d", name, buttonCode);
    }
}
