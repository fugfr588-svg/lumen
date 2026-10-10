package dev.lumen.util;

import java.util.function.BooleanSupplier;

/**
 * Frame-rate independent exponential smoothing. Call {@link #get()} every frame;
 * the value glides towards the target. Used for every animation in the GUI.
 */
public final class Animation {
    /** Lets the user disable animations globally (wired up in LumenClient). */
    public static BooleanSupplier enabled = () -> true;

    private double value, target;
    private long last = System.nanoTime();
    private final double speed;

    public Animation(double initial, double speed) {
        this.value = this.target = initial;
        this.speed = speed;
    }

    public void setTarget(double t) { this.target = t; }
    public double target() { return target; }
    public void snap(double v) { value = target = v; }

    public double get() {
        long now = System.nanoTime();
        double dt = Math.min((now - last) / 1.0e9, 0.1);
        last = now;
        if (!enabled.getAsBoolean()) { value = target; return value; }
        value += (target - value) * (1.0 - Math.exp(-speed * dt));
        if (Math.abs(target - value) < 0.0005) value = target;
        return value;
    }
}
