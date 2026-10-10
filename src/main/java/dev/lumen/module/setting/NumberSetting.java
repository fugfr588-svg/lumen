package dev.lumen.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Locale;

/** Slider value with min / max / step. Values are clamped and snapped to the step on every write. */
public final class NumberSetting extends Setting<Double> {
    private final double min, max, step;

    public NumberSetting(String name, double min, double max, double step, double def) {
        super(name, def);
        this.min = min; this.max = max; this.step = step;
    }

    public double min() { return min; }
    public double max() { return max; }

    @Override
    public void set(Double v) {
        double n = Math.max(min, Math.min(max, v));
        if (step > 0) n = Math.round((n - min) / step) * step + min;
        value = Math.max(min, Math.min(max, n));
    }

    public float getFloat() { return value.floatValue(); }
    public int getInt() { return (int) Math.round(value); }

    public String display() {
        return step >= 1 ? String.valueOf(getInt()) : String.format(Locale.ROOT, "%.1f", value);
    }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }
    @Override public void fromJson(JsonElement e) { set(e.getAsDouble()); }
}
