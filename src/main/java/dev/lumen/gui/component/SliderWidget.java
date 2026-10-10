package dev.lumen.gui.component;

import dev.lumen.module.setting.NumberSetting;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Animation;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;

public final class SliderWidget extends SettingWidget {
    private final NumberSetting s;
    private final Animation fill;
    private boolean dragging;

    public SliderWidget(NumberSetting s) { this.s = s; this.fill = new Animation(frac(), 20); }

    private double frac() { return (s.get() - s.min()) / (s.max() - s.min()); }

    @Override public int height() { return 28; }

    @Override public void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a) {
        place(x, y, w);
        fill.setTarget(frac());
        Draw.text(ctx, s.name(), x, y + 3, Colors.argb(Theme.TEXT, a));
        String v = s.display();
        Draw.text(ctx, v, x + w - Draw.width(v), y + 3, Colors.argb(Theme.TEXT_DIM, a));
        int ty = y + 17;
        Draw.roundedRect(ctx, x, ty, w, 4, 2, Colors.argb(0xFFFFFF, 0.10f * a));
        int fw = (int) Math.round(w * fill.get());
        if (fw > 0) Draw.roundedRect(ctx, x, ty, Math.max(fw, 4), 4, 2, Colors.argb(Theme.accent(), a));
        Draw.roundedRect(ctx, x + fw - 3, ty - 2, 6, 8, 3, Colors.argb(0xFFFFFF, a));
    }

    private void update(double mx) {
        double t = Math.max(0, Math.min(1, (mx - x) / w));
        s.set(s.min() + (s.max() - s.min()) * t);
    }

    @Override public boolean mouseClicked(double mx, double my, int b) {
        if (b == 0 && mx >= x && mx < x + w && my >= y + 10 && my < y + 26) { dragging = true; update(mx); return true; }
        return false;
    }
    @Override public void mouseDragged(double mx, double my) { if (dragging) update(mx); }
    @Override public void mouseReleased() { dragging = false; }
}
