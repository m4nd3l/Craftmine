package dev.m4nd3l.craftmine.ui.components;

import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.layout.Alignment;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import dev.m4nd3l.craftmine.ui.layout.Margin;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class UIPanel extends Component {
    private Vector4f color;

    public UIPanel(Dimensions dimensions, Alignment alignment, Margin margin, int zIndex, Vector4f color) {
        super(dimensions, alignment, margin, zIndex);
        this.color = color;
    }

    @Override
    protected void drawComponent(UIRenderer renderer) {
        Vector2f pos = absolutePosition;
        Vector2f size = dimensions.getSize();

        renderer.addRect(pos.x, pos.y, size.x, size.y, color, null);
    }
}
