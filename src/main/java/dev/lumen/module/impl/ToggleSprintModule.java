package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.Module;

/** Keeps you sprinting while moving forward. No mixins needed: we just set the sprint flag each tick. */
public final class ToggleSprintModule extends Module {
    public ToggleSprintModule() { super("Toggle Sprint", "Automatically sprint when moving forward", Category.MOVEMENT); }

    @Override public void onTick() {
        if (mc.player == null || mc.currentScreen != null) return;
        if (mc.options.forwardKey.isPressed() && !mc.player.isSneaking() && !mc.player.horizontalCollision
                && !mc.player.isUsingItem() && mc.player.getHungerManager().getFoodLevel() > 6) {
            mc.player.setSprinting(true);
        }
    }
}
