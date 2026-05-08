package dev.m4nd3l.craftmine.ui.components.visual;

import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.ui.Component;
import dev.m4nd3l.craftmine.ui.design.UIColor;
import dev.m4nd3l.craftmine.ui.design.UIColors;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import org.joml.Vector2f;

public class UIPanel extends Component {
    private UIColor color;

    public UIPanel(Dimensions dimensions) { this(dimensions, new UIColor(UIColors.RED)); }
    public UIPanel(Dimensions dimensions, UIColors color) { this(dimensions, new UIColor(color)); }
    public UIPanel(Dimensions dimensions, UIColor color) {
        super(dimensions);
        this.color = color;
    }

    public UIPanel setColor(UIColors color) { return setColor(new UIColor(color)); }
    public UIPanel setColor(UIColor color) { this.color = color; pushRenderingAgain(); return this; }

    @Override
    public void pushRendering(UIRenderer renderer) {
        Vector2f pos = absolutePosition;
        Vector2f size = dimensions.getSize();

        renderer.addRect(pos.x, pos.y, size.x, size.y, color, null);
        finishPushRendering(renderer);
    }
}
