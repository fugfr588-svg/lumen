package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import net.minecraft.client.gui.DrawContext;

public final class FpsModule extends HudModule {
    public FpsModule() { super("FPS", "Shows your frame rate", Category.HUD, 6, 6); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        labelValue(ctx, "FPS", String.valueOf(mc.getCurrentFps()));
    }
}
