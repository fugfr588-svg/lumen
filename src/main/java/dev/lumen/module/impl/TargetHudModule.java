package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import dev.lumen.module.setting.NumberSetting;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Animation;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

import java.util.Locale;

/** Shows name + health of the last entity you hit. Fed by AttackEntityCallback (see ClientEvents). */
public final class TargetHudModule extends HudModule {
    private static LivingEntity target;
    private static long lastHit;

    private final NumberSetting duration = add(new NumberSetting("Duration (s)", 1, 10, 1, 4));
    private final Animation health = new Animation(1, 12);

    public TargetHudModule() { super("Target HUD", "Health of the player you last hit", Category.COMBAT, 6, 160); }

    public static void onAttack(Entity e) {
        if (e instanceof LivingEntity le) { target = le; lastHit = System.currentTimeMillis(); }
    }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        size(120, 34);
        boolean live = target != null && target.isAlive()
                && System.currentTimeMillis() - lastHit < duration.get() * 1000;
        String name; float hp, max;
        if (live) { name = target.getName().getString(); hp = target.getHealth(); max = target.getMaxHealth(); }
        else if (editing) { name = "Player"; hp = 14; max = 20; }
        else return;

        Draw.roundedRect(ctx, x, y, width, height, 5, Colors.argb(Theme.HUD_BG, 0.7f));
        Draw.text(ctx, name, x + 6, y + 6, Colors.opaque(Theme.TEXT));
        String hs = String.format(Locale.ROOT, "%.1f", hp);
        Draw.text(ctx, hs, x + width - 6 - Draw.width(hs), y + 6, Colors.opaque(Theme.TEXT_DIM));

        health.setTarget(Math.max(0, Math.min(1, hp / max)));
        int bw = width - 12;
        Draw.roundedRect(ctx, x + 6, y + 21, bw, 6, 3, Colors.argb(0xFFFFFF, 0.10f));
        int fill = (int) (bw * health.get());
        if (fill > 0) Draw.roundedRect(ctx, x + 6, y + 21, Math.max(fill, 4), 6, 3, Colors.opaque(Theme.accent()));
    }
}
