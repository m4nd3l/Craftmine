package dev.m4nd3l.craftmine;

import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.global.Settings;
import dev.m4nd3l.craftmine.renderer.input.*;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.ui.Frame;
import dev.m4nd3l.craftmine.ui.UIManager;
import dev.m4nd3l.craftmine.ui.components.UIImage;
import dev.m4nd3l.craftmine.ui.components.UIPanel;
import dev.m4nd3l.craftmine.ui.design.UIColor;
import dev.m4nd3l.craftmine.ui.design.UIColors;
import dev.m4nd3l.craftmine.ui.layout.Alignment;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import dev.m4nd3l.craftmine.ui.layout.Margin;
import dev.m4nd3l.craftmine.world.World;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.opengl.GL11.*;

public class Craftmine {
    public World currentWorld;
    public UIManager UIManager;
    public boolean wireframeMode;
    public boolean debug;

    public Craftmine() { this(false); }
    public Craftmine(boolean debug) {
        this.currentWorld = null;
        this.wireframeMode = false;
        this.debug = debug;
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

        UIPanel panel = new UIPanel(
                new Dimensions(30, 30, screen),
                Alignment.CENTER,
                new Margin(0),
                1,
                new UIColor(UIColors.RED)
        );
        UIImage image = new UIImage(
                new Dimensions(100, 100, panel.getDimensions().getSize()),
                new MFile("assets", "textures", "blockAtlas.png"));
        image.setMargin(new Margin(0)).setZIndex(99);
        panel.addComponent(image);
        Frame frame = new Frame(panel);

        //UIManager.addFrame(frame);

        UIManager.resize((int)screen.x, (int)screen.y);
        UIManager.pushRendering();

        glfwSetWindowSizeCallback(Main.glfwWindow, (_, width, height) -> {
            UIManager.resize(width, height);
            UIManager.pushRendering();
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

        // WORLD
        if(currentWorld != null) currentWorld.update(delta);

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
        glDepthMask(true); // Ensure we can write to the buffer
        glDisable(GL_CULL_FACE); // Ensure UI faces aren't culled
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
}