package dev.m4nd3l.craftmine.renderer;

import dev.m4nd3l.craftmine.Craftmine;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long glfwWindow;
    private Craftmine craftmine;

    public Window(String title, int width, int height, boolean maximized, Craftmine craftmine) {
        this.craftmine = craftmine;

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, maximized ? GLFW_TRUE : GLFW_FALSE);

        glfwWindow = glfwCreateWindow(width, height, title, NULL, NULL);

        glfwMakeContextCurrent(glfwWindow);
        GL.createCapabilities();

        glfwSwapInterval(1);

        //glEnable(GL_BLEND);
        //glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void start() {
        glfwShowWindow(glfwWindow);
        craftmine.load(glfwWindow);
        gameLoop();
    }

    public void terminate() {
        craftmine.terminate();

        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void gameLoop() {
        double lastTime = glfwGetTime();
        double lag = 0.0;

        double fpsTimer = 0.0;
        int FPSCounter = 0;

        float timePerFrame = 0.0166666667f;

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);

        while (!glfwWindowShouldClose(glfwWindow)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            lag += deltaTime;

            glfwPollEvents();

            glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

            while (lag >= timePerFrame) {
                craftmine.update(timePerFrame);
                lag -= timePerFrame;
                FPSCounter++;
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            craftmine.render();

            glfwSwapBuffers(glfwWindow);

            fpsTimer += deltaTime;
            if (fpsTimer >= 1.0) {
                System.out.println(FPSCounter);
                FPSCounter= 0;
                fpsTimer = 0;
            }
        }
    }

    public long getGlfwWindow() {
        return glfwWindow;
    }
}
