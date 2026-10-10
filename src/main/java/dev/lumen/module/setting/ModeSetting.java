package dev.lumen.module.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/** Pick one of several named options. */
public final class ModeSetting extends Setting<String> {
    private final String[] modes;

    public ModeSetting(String name, String def, String... modes) { super(name, def); this.modes = modes; }

    private int index() {
        for (int i = 0; i < modes.length; i++) if (modes[i].equals(value)) return i;
        return 0;
    }

    public void next() { value = modes[(index() + 1) % modes.length]; }
    public void prev() { value = modes[(index() + modes.length - 1) % modes.length]; }
    public boolean is(String m) { return value.equals(m); }

    @Override public JsonElement toJson() { return new JsonPrimitive(value); }
    @Override public void fromJson(JsonElement e) {
        String s = e.getAsString();
        for (String m : modes) if (m.equals(s)) { value = s; return; }
    }
}
