package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import dev.lumen.module.setting.BooleanSetting;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Animation;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

/** WASD + mouse buttons; keys fade to the accent colour while held. */
public final class KeystrokesModule extends HudModule {
    private static final int K = 20, G = 2;
    private final BooleanSetting space = add(new BooleanSetting("Space Bar", true));
    private final Map<String, Animation> anims = new HashMap<>();

    public KeystrokesModule() { super("Keystrokes", "Visual WASD / mouse display", Category.COMBAT, 6, 110); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        int w = 3 * K + 2 * G;
        size(w, 3 * K + 2 * G + (space.get() ? G + 10 : 0));
        int r2 = y + K + G, r3 = y + 2 * (K + G);
        key(ctx, "w", "W", mc.options.forwardKey.isPressed(), x + K + G, y, K, K);
        key(ctx, "a", "A", mc.options.leftKey.isPressed(), x, r2, K, K);
        key(ctx, "s", "S", mc.options.backKey.isPressed(), x + K + G, r2, K, K);
        key(ctx, "d", "D", mc.options.rightKey.isPressed(), x + 2 * (K + G), r2, K, K);
        int mw = (w - G) / 2;
        key(ctx, "l", "LMB", mc.options.attackKey.isPressed(), x, r3, mw, K);
        key(ctx, "r", "RMB", mc.options.useKey.isPressed(), x + mw + G, r3, w - mw - G, K);
        if (space.get()) key(ctx, "sp", "", mc.options.jumpKey.isPressed(), x, y + 3 * (K + G), w, 8);
    }

    private void key(DrawContext ctx, String id, String label, boolean down, int kx, int ky, int kw, int kh) {
        Animation a = anims.computeIfAbsent(id, k -> new Animation(0, 18));
        a.setTarget(down ? 1 : 0);
        float t = (float) a.get();
        int bg = Colors.lerp(Colors.argb(Theme.HUD_BG, opacity.getFloat() / 100f + 0.1f), Colors.argb(Theme.accent(), 0.8f), t);
        Draw.roundedRect(ctx, kx, ky, kw, kh, 3, bg);
        if (!label.isEmpty()) {
            Draw.centeredText(ctx, label, kx + kw / 2, ky + (kh - 8) / 2, Colors.lerp(Colors.opaque(Theme.TEXT_DIM), 0xFFFFFFFF, t));
        }
    }
}
