package dev.m4nd3l.craftmine.renderer.input;

import org.lwjgl.glfw.GLFW;

public class Mouse {

    private double x, y;
    private double lastX, lastY;
    private double deltaX, deltaY;
    private double scrollX, scrollY;
    private boolean[] buttons = new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];

    public void init(long window) {
        GLFW.glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            deltaX = xpos - x;
            deltaY = ypos - y;
            lastX = x;
            lastY = y;
            x = xpos;
            y = ypos;
        });

        GLFW.glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button >= 0 && button < buttons.length) buttons[button] = action != GLFW.GLFW_RELEASE;
        });

        GLFW.glfwSetScrollCallback(window, (win, offsetX, offsetY) -> {
            scrollX = offsetX;
            scrollY = offsetY;
        });
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }

    public double getScrollX() { return scrollX; }
    public double getScrollY() { return scrollY; }

    public boolean isButtonUp(int button) {
        return !isButtonDown(button);
    }
    public boolean isButtonUp(MouseKeys button) {
        return !isButtonDown(button);
    }
    public boolean isButtonDown(int code) {
        return code >= 0 && code < buttons.length && buttons[code];
    }
    public boolean isButtonDown(MouseKeys button) {
        int code = button.getButtonCode();
        return code >= 0 && code < buttons.length && buttons[code];
    }

    public void endFrame() {
        deltaX = 0;
        deltaY = 0;
        scrollX = 0;
        scrollY = 0;
    }
}
