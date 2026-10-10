package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.Module;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/** Client-side night vision (purely visual; the server never knows). */
public final class FullbrightModule extends Module {
    private boolean applied;

    public FullbrightModule() { super("Fullbright", "See clearly in the dark", Category.VISUAL); }

    @Override public void onTick() {
        ClientPlayerEntity p = mc.player;
        if (p == null) { applied = false; return; }
        StatusEffectInstance cur = p.getStatusEffect(StatusEffects.NIGHT_VISION);
        if (cur == null || (applied && cur.getDuration() < 400)) {
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0, false, false, false));
            applied = true;
        }
    }

    @Override protected void onDisable() {
        if (applied && mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        applied = false;
    }
}
