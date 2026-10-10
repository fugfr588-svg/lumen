package dev.lumen.module.impl;

import dev.lumen.input.CpsTracker;
import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import dev.lumen.module.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;

public final class CpsModule extends HudModule {
    private final BooleanSetting right = add(new BooleanSetting("Show Right Click", true));

    public CpsModule() { super("CPS", "Clicks per second", Category.COMBAT, 6, 190); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        String v = right.get() ? CpsTracker.left() + " | " + CpsTracker.right() : String.valueOf(CpsTracker.left());
        labelValue(ctx, "CPS", v);
    }
}
