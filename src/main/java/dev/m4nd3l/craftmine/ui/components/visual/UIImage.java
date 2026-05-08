package dev.m4nd3l.craftmine.ui.components.visual;

import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.design.*;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import org.jetbrains.annotations.NotNull;

public class UIImage extends Component {
    private Image image;

    public UIImage(@NotNull Dimensions dimensions) { super(dimensions); image = new Image(); }

    public UIImage setRotation(Rotation rotation) { image.setRotation(rotation); pushRenderingAgain(); return this; }
    public UIImage setFlip(Flip flip) { image.setFlip(flip); pushRenderingAgain(); return this; }
    public UIImage setColor(UIColors color) { return setColor(new UIColor(color)); }
    public UIImage setColor(UIColor color) { image.setColor(color); pushRenderingAgain(); return this; }
    public UIImage setTexture(MFile texture) { return setTexture(texture, true); }
    public UIImage setTexture(Texture texture) { return setTexture(texture, true); }
    public UIImage setTexture(MFile texture, boolean deleteOld) { return setTexture(new Texture(texture, false), deleteOld); }
    public UIImage setTexture(Texture texture, boolean deleteOld) {
        if (deleteOld && image.getTexture() != null) image.getTexture().delete();
        image.setTexture(texture);
        pushRenderingAgain();
        return this;
    }

    @Override
    public void pushRendering(UIRenderer renderer) {
        image.pushRendering(renderer, absolutePosition, dimensions.getSize());
        finishPushRendering(renderer);
    }
}