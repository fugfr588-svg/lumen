package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.hit.EntityHitResult;

import java.util.Locale;

/** Distance (in blocks) of your last hit, measured from your eyes to the point you hit. */
public final class ReachModule extends HudModule {
    private static double reach = -1;

    public ReachModule() { super("Reach", "Distance of your last hit", Category.COMBAT, 6, 230); }

    public static void onAttack(net.minecraft.entity.player.PlayerEntity p, EntityHitResult hit) {
        if (hit != null) reach = p.getEyePos().distanceTo(hit.getPos());
    }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        labelValue(ctx, "REACH", reach < 0 ? "-" : String.format(Locale.ROOT, "%.2f", reach));
    }
}
