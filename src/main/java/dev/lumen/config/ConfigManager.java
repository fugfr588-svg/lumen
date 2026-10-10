package dev.lumen.config;

import com.google.gson.*;
import dev.lumen.LumenClient;
import dev.lumen.module.Module;
import dev.lumen.module.ModuleManager;
import dev.lumen.module.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Persists every module (enabled state, keybind, settings, HUD position) to config/lumen.json.
 * Modules are keyed by name, unknown keys are ignored, so adding settings never breaks old configs.
 */
public final class ConfigManager {
    private ConfigManager() {}

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("lumen.json"); }

    public static void save() {
        JsonObject modules = new JsonObject();
        for (Module m : ModuleManager.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("enabled", m.isEnabled());
            o.addProperty("key", m.key());
            JsonObject s = new JsonObject();
            for (Setting<?> st : m.settings()) s.add(st.name(), st.toJson());
            o.add("settings", s);
            m.saveExtra(o);
            modules.add(m.name(), o);
        }
        JsonObject root = new JsonObject();
        root.add("modules", modules);
        try {
            Files.writeString(file(), GSON.toJson(root));
        } catch (IOException e) {
            LumenClient.LOGGER.error("Failed to save config", e);
        }
    }

    public static void load() {
        if (!Files.exists(file())) return;
        try {
            JsonObject modules = JsonParser.parseString(Files.readString(file())).getAsJsonObject().getAsJsonObject("modules");
            if (modules == null) return;
            for (Module m : ModuleManager.all()) {
                if (!modules.has(m.name())) continue;
                JsonObject o = modules.getAsJsonObject(m.name());
                if (o.has("key")) m.setKey(o.get("key").getAsInt());
                if (o.has("settings")) {
                    JsonObject s = o.getAsJsonObject("settings");
                    for (Setting<?> st : m.settings()) {
                        if (!s.has(st.name())) continue;
                        try { st.fromJson(s.get(st.name())); } catch (RuntimeException ignored) { /* bad value: keep default */ }
                    }
                }
                m.loadExtra(o);
                if (o.has("enabled")) m.setEnabled(o.get("enabled").getAsBoolean());
            }
        } catch (IOException | RuntimeException e) {
            LumenClient.LOGGER.error("Failed to load config (using defaults)", e);
        }
    }
}
