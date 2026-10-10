package dev.lumen.module;

import dev.lumen.module.impl.*;

import java.util.ArrayList;
import java.util.List;

/** Owns every module instance. To add a feature: write a Module subclass and register it here. */
public final class ModuleManager {
    private ModuleManager() {}

    private static final List<Module> MODULES = new ArrayList<>();
    private static InterfaceModule iface;

    public static void init() {
        iface = new InterfaceModule();
        // Combat
        register(new TargetHudModule());
        register(new CpsModule());
        register(new KeystrokesModule());
        register(new ComboModule());
        register(new ReachModule());
        // HUD
        register(new FpsModule());
        register(new PingModule());
        register(new CoordinatesModule());
        register(new ArmorModule());
        register(new PotionsModule());
        // Movement / Visual
        register(new ToggleSprintModule());
        register(new FullbrightModule());
        // Client
        register(iface);
    }

    private static void register(Module m) { MODULES.add(m); }

    public static InterfaceModule iface() { return iface; }
    public static List<Module> all() { return MODULES; }

    public static List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : MODULES) if (m.category() == c) out.add(m);
        return out;
    }

    public static List<HudModule> hudModules() {
        List<HudModule> out = new ArrayList<>();
        for (Module m : MODULES) if (m instanceof HudModule h) out.add(h);
        return out;
    }

    public static void tick() {
        for (Module m : MODULES) if (m.isEnabled()) m.onTick();
    }
}
