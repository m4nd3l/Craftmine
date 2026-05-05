package dev.m4nd3l.craftmine.ui.layout;

import dev.m4nd3l.craftmine.Main;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;

public class Dimensions {
    private float xPercent, yPercent;
    private Vector2f size = new Vector2f();
    private Vector2f parentSizeRef;
    private boolean parentIsComponent;

    public Dimensions(float xPercent, float yPercent, Vector2f parentSize) {
        this.xPercent = xPercent;
        this.yPercent = yPercent;
        this.parentSizeRef = parentSize;
        recalculateSize((int) parentSize.x, (int) parentSize.y);
    }

    public void recalculateSize(float width, float height) {
        if (parentSizeRef == null) parentSizeRef = new Vector2f();
        size.x = width * (xPercent / 100f);
        size.y = height * (yPercent / 100f);
    }

    public Vector2f getSize() { return size; }
    public Vector2f getParentSize() { return parentSizeRef; }

    public static Vector2f getScreenSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer widthB = stack.mallocInt(1);
            IntBuffer heightB = stack.mallocInt(1);

            glfwGetWindowSize(Main.glfwWindow, widthB, heightB);

            int width = widthB.get(0);
            int height = heightB.get(0);
            return new Vector2f(width, height);
        } catch (Exception ignored) {}
        return new Vector2f(1920, 1080);
    }

    public void setParentSize(Vector2f size, boolean parentIsComponent) {
        this.parentSizeRef = size;
        this.parentIsComponent = parentIsComponent;
        recalculateSize(size.x, size.y);
    }
}