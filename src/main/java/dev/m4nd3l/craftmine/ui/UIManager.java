package dev.m4nd3l.craftmine.ui;

import dev.m4nd3l.craftmine.renderer.renderers.UIRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class UIManager {
    private List<Frame> activeFrames;
    private UIRenderer renderer;

    public UIManager() { this(new ArrayList<>()); }
    public UIManager(Frame... frames) { this(Arrays.stream(frames).toList()); }
    public UIManager(List<Frame> activeFrames) {
        this.renderer = new UIRenderer();
        this.activeFrames = new ArrayList<>(activeFrames);
        reorder();
    }

    public UIManager addFrame(Frame frame) { activeFrames.add(frame); reorder(); return this; }
    public UIManager removeFrame(Frame frame) { activeFrames.remove(frame); reorder(); return this; }

    public void update(float deltaTime) {
        boolean mouseCaptured = false;
        for (int i = activeFrames.size() - 1; i >= 0; i--)
            if (activeFrames.get(i).update(deltaTime, mouseCaptured)) mouseCaptured = true;
    }

    public void pushRendering() { activeFrames.forEach(frame -> frame.pushRendering(renderer)); renderer.uploadToGPU(); }
    public void render() { renderer.render(); }
    public void resize(int width, int height) {
        activeFrames.forEach(frame -> frame.resize(width, height));
        renderer.updateSize(width, height);
    }

    private void reorder() { activeFrames.sort(Comparator.comparingInt(Frame::getZIndex)); }

    public void delete() { renderer.delete(); }
}
