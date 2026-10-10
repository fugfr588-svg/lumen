package dev.lumen.module;

import com.google.gson.JsonObject;
import dev.lumen.module.setting.Setting;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class of every feature. A module has a name, a category, an on/off state, an optional
 * keybind and any number of {@link Setting}s which the Click GUI turns into widgets automatically.
 */
public abstract class Module {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();

    private final String name, description;
    private final Category category;
    private final List<Setting<?>> settings = new ArrayList<>();
    private boolean enabled;
    private int key = -1; // GLFW key code, -1 = unbound

    protected Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
    }

    /** Register a setting; call from field initialisers: {@code BooleanSetting x = add(new BooleanSetting(..))}. */
    protected <S extends Setting<?>> S add(S s) { settings.add(s); return s; }

    public String name() { return name; }
    public String description() { return description; }
    public Category category() { return category; }
    public List<Setting<?>> settings() { return Collections.unmodifiableList(settings); }

    /** Modules that are always active (e.g. the Interface settings) return false. */
    public boolean isToggleable() { return true; }
    public boolean isEnabled() { return !isToggleable() || enabled; }

    public void setEnabled(boolean on) {
        if (!isToggleable() || on == enabled) return;
        enabled = on;
        if (on) onEnable(); else onDisable();
    }
    public void toggle() { setEnabled(!enabled); }

    public int key() { return key; }
    public void setKey(int k) { key = k; }

    // ---- lifecycle hooks ----
    protected void onEnable() {}
    protected void onDisable() {}
    /** Called every client tick while enabled. */
    public void onTick() {}

    // ---- persistence extension points (HUD modules store their position here) ----
    public void saveExtra(JsonObject o) {}
    public void loadExtra(JsonObject o) {}
}
