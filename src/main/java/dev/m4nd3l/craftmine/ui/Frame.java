package dev.m4nd3l.craftmine.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Frame {
    private List<Component> activeComponents;
    private int zIndex;

    public Frame() { this(new ArrayList<>()); }
    public Frame(List<Component> activeFrames) { this.activeComponents = activeFrames; reorder(); }

    public Frame addComponent(Component component) { activeComponents.add(component); reorder(); return this; }
    public Frame removeComponent(Component component) { activeComponents.remove(component); reorder(); return this; }

    public boolean update(float deltaTime, boolean mouseCaptured) {
        boolean capturedInFrame = false;
        for (int i = activeComponents.size() - 1; i >= 0; i--)
            if (activeComponents.get(i).update(deltaTime, mouseCaptured || capturedInFrame)) capturedInFrame = true;
        return capturedInFrame;
    }
    public void render() { activeComponents.forEach(Component::render); }
    public void resize(int width, int height) { activeComponents.forEach(component -> component.resize(width, height, 0, 0)); }

    private void reorder() { activeComponents.sort(Comparator.comparingInt(Component::getZIndex)); }

    public int getZIndex() { return zIndex; }
}
