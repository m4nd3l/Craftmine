package dev.m4nd3l.craftmine.ui.design;

import java.util.*;

public enum RotationValue {
    D_0(0.0f),

    D_90(90.0f),
    D_180(180.0f),
    D_270(270.0f),

    D_M90(-90.0f),
    D_M180(-180.0f),
    D_M270(-270.0f);

    private float value;
    RotationValue(float value) { this.value = value; }
    public float getValue() { return value; }

    private static Map<Float, RotationValue> values;

    static {
        values = new HashMap<>();
        List<RotationValue> valuesList = new ArrayList<>(Arrays.stream(RotationValue.values()).toList());
        valuesList.sort(Comparator.comparingDouble(RotationValue::getValue));
        valuesList.forEach(value -> values.put(value.getValue(), value));
    }

    public static RotationValue findNearest(float degrees) {
        RotationValue bestLeft = null, bestRight = null;
        float maxLeftKey = -Float.MAX_VALUE, minRightKey = Float.MAX_VALUE;

        for (Map.Entry<Float, RotationValue> entry : values.entrySet()) {
            float key = entry.getKey();

            if (key <= degrees) {
                if (key > maxLeftKey) {
                    maxLeftKey = key;
                    bestLeft = entry.getValue();
                }
            } else {
                if (key < minRightKey) {
                    minRightKey = key;
                    bestRight = entry.getValue();
                }
            }
        }

        if (bestLeft == null) return bestRight;
        if (bestRight == null) return bestLeft;

        return (minRightKey - degrees) > (degrees - maxLeftKey) ? bestLeft : bestRight;
    }
}
