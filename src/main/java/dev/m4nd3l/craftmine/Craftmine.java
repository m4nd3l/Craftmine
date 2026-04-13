package dev.m4nd3l.craftmine;

import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.renderer.input.*;
import dev.m4nd3l.craftmine.world.World;

import static org.lwjgl.opengl.GL11.*;

public class Craftmine {
    private World currentWorld;
    private long glfwWindow;
    private boolean wireframeMode, debug;

    public Craftmine(boolean debug) {
        currentWorld = null;
        wireframeMode = false;
        this.debug = debug;
    }

    public Craftmine() { this(false); }

    public World getCurrentWorld() { return currentWorld; }

    public void load(long glfwWindow) {
        this.glfwWindow = glfwWindow;
        Input.initialize(glfwWindow);

        // TODO START MENU
        currentWorld = new World(
                glfwWindow,
                "default");
    }

    public void update(float delta) {
        if(currentWorld != null) currentWorld.update(delta);

        if (Input.keyboard.isKeyPressed(KeyboardKeys.L) &&
            Input.keyboard.isControlDown()
            && debug) wireframeMode = !wireframeMode;

        Input.update();
        Input.endFrame();
    }

    public void render() {
        if (wireframeMode && debug) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        if (currentWorld != null) currentWorld.render();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void terminate() { if (currentWorld != null) currentWorld.delete(); }
}
