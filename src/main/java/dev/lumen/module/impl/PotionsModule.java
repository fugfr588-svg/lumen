package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;

/** Active potion effects with level and time left. */
public final class PotionsModule extends HudModule {
    public PotionsModule() { super("Potions", "Active effects and remaining time", Category.HUD, 6, 160); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        List<String> names = new ArrayList<>(), times = new ArrayList<>();
        if (mc.player != null) {
            for (StatusEffectInstance e : mc.player.getStatusEffects()) {
                names.add(e.getEffectType().value().getName().getString() + " " + (e.getAmplifier() + 1));
                int s = e.getDuration() / 20;
                times.add(e.isInfinite() ? "**" : String.format("%d:%02d", s / 60, s % 60));
            }
        }
        if (names.isEmpty()) {
            if (!editing) return;
            names.add("Speed 2"); times.add("1:30");
        }
        int w = 0;
        for (int i = 0; i < names.size(); i++) w = Math.max(w, Draw.width(names.get(i)) + 10 + Draw.width(times.get(i)));
        size(w + 12, names.size() * 11 + 6);
        box(ctx);
        for (int i = 0; i < names.size(); i++) {
            int yy = y + 4 + i * 11;
            Draw.text(ctx, names.get(i), x + 6, yy, Colors.opaque(Theme.TEXT), !background.get());
            Draw.text(ctx, times.get(i), x + width - 6 - Draw.width(times.get(i)), yy, Colors.opaque(Theme.accent()), !background.get());
        }
    }
}
