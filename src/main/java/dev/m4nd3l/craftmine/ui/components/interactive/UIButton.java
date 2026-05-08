package dev.m4nd3l.craftmine.ui.components.interactive;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.design.*;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class UIButton extends Component {
    private Image normal, hover, click;
    private CoordinatesLambdaComponent onClick = (_, _) -> {};
    private CoordinatesLambdaComponent onClickHold = (_, _) -> {};
    private CoordinatesLambdaComponent onHover = (_, _) -> {};
    private CoordinatesLambdaComponent onHoverHold = (_, _) -> {};
    private int current;

    public UIButton(@NotNull Dimensions dimensions) { super(dimensions); normal = new Image(); hover = new Image(); click = new Image(); current = 0; }

    public Image getNormal() { return normal; }
    public Image getHover() { return hover; }
    public Image getClick() { return click; }

    public UIButton setNormalImageTexture(MFile texture) { return setNormalImageTexture(texture, true); }
    public UIButton setNormalImageTexture(Texture texture) { return setNormalImageTexture(texture, true); }
    public UIButton setNormalImageTexture(MFile texture, boolean deleteOld) { return setNormalImageTexture(new Texture(texture, false), deleteOld); }
    public UIButton setNormalImageTexture(Texture texture, boolean deleteOld) { return setTexture(normal, texture, deleteOld); }

    public UIButton setHoverImageTexture(MFile texture) { return setHoverImageTexture(texture, true); }
    public UIButton setHoverImageTexture(Texture texture) { return setHoverImageTexture(texture, true); }
    public UIButton setHoverImageTexture(MFile texture, boolean deleteOld) { return setHoverImageTexture(new Texture(texture, false), deleteOld); }
    public UIButton setHoverImageTexture(Texture texture, boolean deleteOld) { return setTexture(hover, texture, deleteOld); }

    public UIButton setClickImageTexture(MFile texture) { return setClickImageTexture(texture, true); }
    public UIButton setClickImageTexture(Texture texture) { return setClickImageTexture(texture, true); }
    public UIButton setClickImageTexture(MFile texture, boolean deleteOld) { return setClickImageTexture(new Texture(texture, false), deleteOld); }
    public UIButton setClickImageTexture(Texture texture, boolean deleteOld) { return setTexture(click, texture, deleteOld); }

    public UIButton setOnClick(CoordinatesLambdaComponent onClick) { this.onClick = onClick; return this; }
    public UIButton setOnClickHold(CoordinatesLambdaComponent onClickHold) { this.onClickHold = onClickHold; return this; }
    public UIButton setOnHover(CoordinatesLambdaComponent onHover) { this.onHover = onHover; return this; }
    public UIButton setOnHoverHold(CoordinatesLambdaComponent onHoverHold) { this.onHoverHold = onHoverHold; return this; }

    @Override public void onClick(float x, float y) { onClick.invoke(x, y); }
    @Override public void onClickHold(float x, float y) { onClickHold.invoke(x, y); }
    @Override public void onHover(float x, float y) { onHover.invoke(x, y); }
    @Override public void onHoverHold(float x, float y) { onHoverHold.invoke(x, y); }

    @Override
    public boolean update(float deltaTime, boolean alreadyCaptured) {
        boolean toReturn = super.update(deltaTime, alreadyCaptured);
        if (GLFW.glfwGetInputMode(Main.glfwWindow, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_NORMAL) return toReturn;
        if (onClickCalled) setCurrent(2);
        else if (onHoverCalled) setCurrent(1);
        else setCurrent(0);
        return toReturn;
    }

    @Override
    public void pushRendering(UIRenderer renderer) { getImage().pushRendering(renderer, absolutePosition, dimensions.getSize()); }

    private int setCurrent(int newIndex) { current = newIndex; pushRenderingAgain(); return current; }
    private Image getImage() {
        return switch (current) {
            case 1 -> hover;
            case 2 -> click;
            default -> normal;
        };
    }

    private UIButton setTexture(Image involved, Texture texture, boolean deleteOld) {
        if (deleteOld && involved.getTexture() != null) involved.getTexture().delete();
        involved.setTexture(texture);
        pushRenderingAgain();
        return this;
    }
}
