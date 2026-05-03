package dev.m4nd3l.craftmine.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UIManager {
    private List<Frame> activeFrames;

    public UIManager() { this(new ArrayList<>()); }
    public UIManager(List<Frame> activeFrames) { this.activeFrames = activeFrames; reorder(); }

    public UIManager addFrame(Frame frame) { activeFrames.add(frame); reorder(); return this; }
    public UIManager removeFrame(Frame frame) { activeFrames.remove(frame); reorder(); return this; }

    public void update(float deltaTime) {
        boolean mouseCaptured = false;
        for (int i = activeFrames.size() - 1; i >= 0; i--)
            if (activeFrames.get(i).update(deltaTime, mouseCaptured)) mouseCaptured = true;
    }
    public void render() { activeFrames.forEach(Frame::render); }
    public void resize(int width, int height) { activeFrames.forEach(frame -> frame.resize(width, height)); }

    private void reorder() { activeFrames.sort(Comparator.comparingInt(Frame::getZIndex)); }
}
