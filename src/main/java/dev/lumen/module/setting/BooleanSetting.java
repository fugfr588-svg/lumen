package dev.lumen.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public final class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean def) { super(name, def); }

    public void toggle() { set(!get()); }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }
    @Override public void fromJson(JsonElement e) { value = e.getAsBoolean(); }
}
