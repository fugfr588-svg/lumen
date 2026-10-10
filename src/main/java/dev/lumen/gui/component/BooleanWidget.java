package dev.lumen.gui.component;

import dev.lumen.module.setting.BooleanSetting;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Animation;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;

public final class BooleanWidget extends SettingWidget {
    private final BooleanSetting s;
    private final Animation anim;

    public BooleanWidget(BooleanSetting s) { this.s = s; this.anim = new Animation(s.get() ? 1 : 0, 18); }

    @Override public int height() { return 20; }

    @Override public void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a) {
        place(x, y, w);
        anim.setTarget(s.get() ? 1 : 0);
        Draw.text(ctx, s.name(), x, y + 6, Colors.argb(Theme.TEXT, a));
        Draw.toggle(ctx, x + w - 22, y + 4, 22, 12, (float) anim.get(), a);
    }

    @Override public boolean mouseClicked(double mx, double my, int b) {
        if (b == 0 && hit(mx, my)) { s.toggle(); return true; }
        return false;
    }
}
