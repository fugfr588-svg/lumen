package dev.lumen.gui.component;

import dev.lumen.module.setting.ModeSetting;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;

/** Click to cycle forward, right-click to cycle back. */
public final class ModeWidget extends SettingWidget {
    private final ModeSetting s;
    public ModeWidget(ModeSetting s) { this.s = s; }

    @Override public int height() { return 20; }

    @Override public void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a) {
        place(x, y, w);
        Draw.text(ctx, s.name(), x, y + 6, Colors.argb(Theme.TEXT, a));
        String v = s.get();
        int pw = Draw.width(v) + 12;
        Draw.roundedRect(ctx, x + w - pw, y + 3, pw, 14, 4, Colors.argb(0xFFFFFF, 0.08f * a));
        Draw.text(ctx, v, x + w - pw + 6, y + 6, Colors.argb(Theme.accent(), a));
    }

    @Override public boolean mouseClicked(double mx, double my, int b) {
        if (!hit(mx, my)) return false;
        if (b == 0) s.next(); else if (b == 1) s.prev();
        return true;
    }
}
