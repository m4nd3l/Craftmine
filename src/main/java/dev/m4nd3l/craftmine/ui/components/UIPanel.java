package dev.m4nd3l.craftmine.ui.components;

import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.design.UIColor;
import dev.m4nd3l.craftmine.ui.layout.Alignment;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import dev.m4nd3l.craftmine.ui.layout.Margin;
import org.joml.Vector2f;

public class UIPanel extends Component {
    private UIColor color;

    public UIPanel(Dimensions dimensions, Alignment alignment, Margin margin, int zIndex, UIColor color) {
        super(dimensions, alignment, margin, zIndex);
        this.color = color;
    }

    @Override
    public void pushRendering(UIRenderer renderer) {
        Vector2f pos = absolutePosition;
        Vector2f size = dimensions.getSize();

        renderer.addRect(pos.x, pos.y, size.x, size.y, color, null);
        finishPushRendering(renderer);
    }
}
