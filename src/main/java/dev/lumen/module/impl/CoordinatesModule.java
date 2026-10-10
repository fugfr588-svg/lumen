package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import dev.lumen.module.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

import java.util.Locale;

public final class CoordinatesModule extends HudModule {
    private final BooleanSetting direction = add(new BooleanSetting("Show Direction", true));

    public CoordinatesModule() { super("Coordinates", "Shows your block position", Category.HUD, 6, 46); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        String s = "0  0  0";
        if (mc.player != null) {
            s = mc.player.getBlockX() + "  " + mc.player.getBlockY() + "  " + mc.player.getBlockZ();
            if (direction.get()) {
                s += "  " + mc.player.getHorizontalFacing().asString().substring(0, 1).toUpperCase(Locale.ROOT);
            }
        }
        labelValue(ctx, "XYZ", s);
    }
}
