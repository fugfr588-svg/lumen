package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import net.minecraft.client.gui.DrawContext;

/** Counts consecutive hits you land; resets when you get hit or after a few seconds without hitting. */
public final class ComboModule extends HudModule {
    private static int combo;
    private static long lastHit;
    private int lastHurt;

    public ComboModule() { super("Combo", "Consecutive hits without being hit", Category.COMBAT, 6, 210); }

    public static void onAttack() { combo++; lastHit = System.currentTimeMillis(); }

    @Override public void onTick() {
        if (mc.player == null) return;
        int hurt = mc.player.hurtTime;
        if (hurt > 0 && lastHurt == 0) combo = 0;   // we just took damage
        lastHurt = hurt;
        if (combo > 0 && System.currentTimeMillis() - lastHit > 4000) combo = 0;
    }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        labelValue(ctx, "COMBO", String.valueOf(combo));
    }
}
