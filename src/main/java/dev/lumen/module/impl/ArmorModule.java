package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import dev.lumen.render.Draw;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

/** Armour pieces with durability percentage. */
public final class ArmorModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public ArmorModule() { super("Armor", "Armour and durability", Category.HUD, 6, 70); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        size(52, 4 * 18 + 4);
        box(ctx);
        int yy = y + 3;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack st = mc.player == null ? ItemStack.EMPTY : mc.player.getEquippedStack(slot);
            if (st.isEmpty()) {
                Draw.roundedRect(ctx, x + 5, yy + 1, 14, 14, 3, Colors.argb(0xFFFFFF, 0.06f));
            } else {
                ctx.drawItem(st, x + 4, yy);
                if (st.isDamageable()) {
                    int max = st.getMaxDamage();
                    float pct = (max - st.getDamage()) / (float) max;
                    int col = Colors.lerp(0xFFFF5555, 0xFF55FF77, pct);
                    Draw.text(ctx, Math.round(pct * 100) + "%", x + 23, yy + 4, col, !background.get());
                }
            }
            yy += 18;
        }
    }
}
