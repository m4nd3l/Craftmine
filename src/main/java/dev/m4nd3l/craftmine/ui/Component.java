package dev.m4nd3l.craftmine.ui;

import dev.m4nd3l.craftmine.Main;
import dev.m4nd3l.craftmine.global.Input;
import dev.m4nd3l.craftmine.renderer.input.MouseKeys;
import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;
import dev.m4nd3l.craftmine.ui.layout.Alignment;
import dev.m4nd3l.craftmine.ui.layout.Dimensions;
import dev.m4nd3l.craftmine.ui.layout.Margin;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Comparator;
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

    public Component addComponent(Component component) { component.setParentSize(dimensions.getSize()); children.add(component); reorder(); return this; }

    public Component removeComponent(Component component) { children.remove(component); reorder(); return this; }
    private void reorder() { children.sort(Comparator.comparingInt(Component::getZIndex)); }

    public void onClick(float x, float y) { }
    public void onHover(float x, float y) { }
    public void onHoverHold(float x, float y) { }
    public void onClickHold(float x, float y) { }

    public abstract void pushRendering(UIRenderer renderer);
    public void finishPushRendering(UIRenderer renderer) {
        children.forEach(children -> children.pushRendering(renderer));
    }

    public void resize(int parentWidth, int parentHeight, float parentX, float parentY) {
        dimensions.recalculateSize(parentWidth, parentHeight);
        Vector2f offset = alignment != null ? alignment.getOffset(dimensions, margin) : new Vector2f(0.0f, 0.0f);
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
    public void pushRenderingAgain() { Main.craftmine.UIManager.pushRendering(); }

    private boolean isHovered() {
        float mouseX = (float) Input.mouse.getX();
        float mouseY = (float) Input.mouse.getY();
        Vector2f size = dimensions.getSize();
        return mouseX >= absolutePosition.x &&
               mouseX <= absolutePosition.x + size.x &&
               mouseY >= absolutePosition.y &&
               mouseY <= absolutePosition.y + size.y;
    }

    public Vector2f getAbsolutePosition() { return absolutePosition; }
    public Alignment getAlignment() { return alignment; }
    public Dimensions getDimensions() { return dimensions; }
    public Margin getMargin() { return margin; }
    public int getzIndex() { return zIndex; }

    public Component setAlignment(Alignment alignment) { this.alignment = alignment; return this; }
    public Component setDimensions(Dimensions dimensions) { this.dimensions = dimensions; return this; }
    public Component setMargin(Margin margin) { this.margin = margin; return this; }
    public Component setZIndex(int zIndex) { this.zIndex = zIndex; return this; }
    private void setParentSize(Vector2f size) { dimensions.setParentSize(size, true); }
}