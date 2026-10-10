package dev.lumen.render;

import dev.lumen.module.ModuleManager;
import dev.lumen.module.impl.InterfaceModule;
import dev.lumen.util.Colors;

/** Central colour palette. The accent is user-configurable (Client > Interface). */
public final class Theme {
    private Theme() {}

    public static final int TEXT = 0xE8E8EE;
    public static final int TEXT_DIM = 0x8C8C98;
    public static final int PANEL = 0x0F0F13;
    public static final int HUD_BG = 0x0B0B0F;

    /** Current accent as 0xRRGGBB. */
    public static int accent() {
        InterfaceModule i = ModuleManager.iface();
        return Colors.hsb(i.hue.getFloat() / 360f, i.saturation.getFloat() / 100f, 1f);
    }
}
