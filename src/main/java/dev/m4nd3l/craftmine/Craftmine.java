package dev.m4nd3l.craftmine;

import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.global.Settings;
import dev.m4nd3l.craftmine.renderer.input.*;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.ui.Frame;
import dev.m4nd3l.craftmine.ui.UIManager;
import dev.m4nd3l.craftmine.ui.components.interactive.UIButton;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import dev.m4nd3l.craftmine.world.World;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Craftmine {
    public World currentWorld;
    public UIManager UIManager;
    public boolean wireframeMode, debug, onPause;

    public Craftmine() { this(false); }
    public Craftmine(boolean debug) {
        this.currentWorld = null;
        this.wireframeMode = false;
        this.debug = debug;
        this.onPause = false;
    }

    public World getCurrentWorld() { return currentWorld; }

    public void load(long glfwWindow) {
        // INPUT
        Input.initialize(glfwWindow);

        // ENTITIES
        glLineWidth(Settings.settings.getHitboxesLinesWidth());

        // UI
        this.UIManager = new UIManager();
        Vector2f screen = Dimensions.getScreenSize();

        UIButton button = new UIButton(new Dimensions(30, 30, screen))
                .setNormalImageTexture(new MFile("assets", "textures", "ui", "button.png"))
                .setHoverImageTexture(new MFile("assets", "textures", "ui", "button_highlighted.png"))
                .setClickImageTexture(new MFile("assets", "textures", "blockAtlas.png"))
                .setOnClick((_, _) -> System.out.println("-------- OnClick has been triggered"))
                .setOnHover((_, _) -> System.out.println("-------- OnHover has been triggered"));
        Frame frame = new Frame(button);

        UIManager.addFrame(frame);

        UIManager.resize((int) screen.x, (int) screen.y);
        UIManager.pushRendering();

        glfwSetFramebufferSizeCallback(Main.glfwWindow, (window, width, height) -> {
            UIManager.resize(width, height);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            render();
            glfwSwapBuffers(window);
        });

        // WORLD
        // TODO START MENU
        currentWorld = new World("default");
    }

    public void update(float delta) {
        // INPUT
        if (Input.keyboard.isKeyPressed(KeyboardKeys.L) &&
                Input.keyboard.isControlDown()
                && debug) wireframeMode = !wireframeMode;
        if (Input.keyboard.isKeyPressed(KeyboardKeys.ESC)) {
            onPause = !onPause;
            GLFW.glfwSetInputMode(Main.glfwWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL);
        }

        // UI
        UIManager.update(delta);

        // WORLD
        if (currentWorld != null && !onPause) currentWorld.update(delta);

        // END FRAME
        Input.update();
        Input.endFrame();
    }

    public void render() {
        // WIREFRAMES
        if (wireframeMode && debug) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        else glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // WORLD
        if (currentWorld != null) currentWorld.render();

        // UI
        glDisable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        UIManager.pushRendering();
        UIManager.render();

        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);

        // OTHER
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void terminate() {
        if (currentWorld != null) currentWorld.delete();
        UIManager.delete();
    }

    public void tick() {
        if(currentWorld != null) currentWorld.tick();
    }
}