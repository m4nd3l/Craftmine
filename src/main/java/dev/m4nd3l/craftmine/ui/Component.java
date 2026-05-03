package dev.m4nd3l.craftmine.ui;

import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.renderer.input.MouseKeys;
import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.ui.layout.Alignment;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import dev.m4nd3l.craftmine.ui.layout.Margin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
    protected List<Component> children;
    protected Vector2f absolutePosition;
    protected Dimensions dimensions;
    protected Margin margin;
    protected Alignment alignment;
    protected int zIndex;

    private boolean onHoverCalled, onClickCalled;

    public Component(@NotNull Dimensions dimensions, Alignment alignment, Margin margin, int zIndex) {
        this.dimensions = dimensions;
        this.margin = margin == null ? new Margin(0) : margin;
        this.alignment = alignment == null ? Alignment.CENTER : alignment;
        this.zIndex = zIndex == -1 ? 0 : zIndex;
        this.children = new ArrayList<>();
        this.absolutePosition = new Vector2f();
        this.onHoverCalled = false;
        this.onClickCalled = false;
    }

    public void onClick(float x, float y) { }
    public void onHover(float x, float y) { }
    public void onHoverHold(float x, float y) { }
    public void onClickHold(float x, float y) { }

    public void render(UIRenderer renderer) {
        drawComponent(renderer);
        children.forEach(component -> component.render(renderer));
    }

    public void resize(int parentWidth, int parentHeight, float parentX, float parentY) {
        dimensions.recalculateSize(parentWidth, parentHeight);
        Vector2f offset = alignment.getOffset(dimensions, margin);
        this.absolutePosition.set(parentX + offset.x, parentY + offset.y);
        Vector2f mySize = dimensions.getSize();
        for (Component child : children) child.resize((int) mySize.x, (int) mySize.y, absolutePosition.x, absolutePosition.y);
    }

    public boolean update(float deltaTime, boolean alreadyCaptured) {
        boolean hovered = !alreadyCaptured && isHovered();
        boolean capturedInChildren = false;

        for (int i = children.size() - 1; i >= 0; i--)
            if (children.get(i).update(deltaTime, alreadyCaptured || hovered || capturedInChildren))
                capturedInChildren = true;

        if (hovered) {
            if (Input.mouse.isButtonDown(MouseKeys.LEFT)) {
                if (!onClickCalled) onClick((float) Input.mouse.getX(), (float) Input.mouse.getY());
                onClickCalled = true;
                onClickHold((float) Input.mouse.getX(), (float) Input.mouse.getY());
            } else onClickCalled = false;

            if (!onHoverCalled) onHover((float) Input.mouse.getX(), (float) Input.mouse.getY());
            onHoverCalled = true;
            onHoverHold((float) Input.mouse.getX(), (float) Input.mouse.getY());
        } else {
            onClickCalled = false;
            onHoverCalled = false;
        }

        return hovered || capturedInChildren;
    }

    public int getZIndex() { return zIndex; }

    private boolean isHovered() {
        float mouseX = (float) Input.mouse.getX();
        float mouseY = (float) Input.mouse.getY();
        Vector2f size = dimensions.getSize();
        return mouseX >= absolutePosition.x &&
                mouseX <= absolutePosition.x + size.x &&
                mouseY >= absolutePosition.y &&
                mouseY <= absolutePosition.y + size.y;
    }


    protected abstract void drawComponent(UIRenderer renderer);
}
