package dev.m4nd3l.craftmine.ui.components;

import dev.m4nd3l.craftmine.renderer.opengl.Texture;
import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.renderer.util.MFile;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.design.UIColor;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;

public class UIImage extends Component {
    private UIColor color = new UIColor(1, 1, 1, 1);
    private Texture texture;

    public UIImage(Dimensions dimensions, MFile textureFile) {
        super(dimensions, null, null, -1);
        this.texture = new Texture(textureFile, false);
    }

    @Override
    public void pushRendering(UIRenderer renderer) {
        renderer.addRect(
                absolutePosition.x, absolutePosition.y,
                dimensions.getSize().x, dimensions.getSize().y,
                color, texture
        );
        finishPushRendering(renderer);
    }
}