package dev.m4nd3l.craftmine.ui.components;

import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import org.joml.Vector4f;

public class UIImage extends Component {
    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Vector4f uvRect;
    private UIRenderer renderer;

    public UIImage(Dimensions dimensions, Vector4f uvRect, UIRenderer renderer) {
        super(dimensions, null, null, -1);
        this.uvRect = uvRect;
        this.renderer = renderer;
    }

    @Override
    protected void drawComponent(UIRenderer renderer) {
        renderer.addRect(
                absolutePosition.x, absolutePosition.y,
                dimensions.getSize().x, dimensions.getSize().y,
                color, uvRect
        );
    }
}
