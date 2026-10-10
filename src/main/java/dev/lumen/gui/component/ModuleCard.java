package dev.lumen.gui.component;

import dev.lumen.module.Module;
import dev.lumen.module.setting.*;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Animation;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** A module row: name, description and switch; right-click expands an animated settings drawer. */
public final class ModuleCard {
    private static final int HEADER = 32;

    private final Module module;
    private final List<SettingWidget> widgets = new ArrayList<>();
    private final Animation expand = new Animation(0, 16);
    private final Animation on;
    private final Animation hover = new Animation(0, 20);
    private boolean expanded;
    private int x, y, w, total = HEADER;

    public ModuleCard(Module m, Consumer<KeybindWidget> onListen) {
        this.module = m;
        this.on = new Animation(m.isEnabled() ? 1 : 0, 18);
        for (Setting<?> s : m.settings()) {
            if (s instanceof BooleanSetting b) widgets.add(new BooleanWidget(b));
            else if (s instanceof NumberSetting n) widgets.add(new SliderWidget(n));
            else if (s instanceof ModeSetting md) widgets.add(new ModeWidget(md));
        }
        if (m.isToggleable()) widgets.add(new KeybindWidget(m, onListen));
    }

    private int contentHeight() {
        int h = 8;
        for (SettingWidget sw : widgets) h += sw.height();
        return h;
    }

    /** @return the height this card occupied (the screen stacks cards using it). */
    public int render(DrawContext ctx, int x, int y, int w, int mx, int my, float a, boolean interactive) {
        this.x = x; this.y = y; this.w = w;
        int content = contentHeight();
        expand.setTarget(expanded ? 1 : 0);
        int vis = (int) Math.round(content * expand.get());
        total = HEADER + vis;

        boolean hov = interactive && mx >= x && mx < x + w && my >= y && my < y + HEADER;
        hover.setTarget(hov ? 1 : 0);
        on.setTarget(module.isEnabled() ? 1 : 0);
        float ht = (float) hover.get(), ot = (float) on.get();

        Draw.roundedRect(ctx, x, y, w, total, 6, Colors.argb(0xFFFFFF, (0.045f + 0.035f * ht) * a));
        if (module.isToggleable() && ot > 0.01f) { // thin accent marker for enabled modules
            ctx.fill(x, y + 8, x + 2, y + HEADER - 8, Colors.argb(Theme.accent(), ot * a));
        }
        Draw.text(ctx, module.name(), x + 12, y + 7,
                Colors.argb(Colors.lerp(Theme.TEXT_DIM | 0xFF000000, Theme.TEXT | 0xFF000000, module.isToggleable() ? ot : 1) & 0xFFFFFF, a));
        Draw.text(ctx, module.description(), x + 12, y + 19, Colors.argb(Theme.TEXT_DIM, a * 0.8f));
        if (module.isToggleable()) Draw.toggle(ctx, x + w - 38, y + HEADER / 2 - 6, 26, 12, ot, a);
        if (!widgets.isEmpty()) {
            Draw.text(ctx, expanded ? "v" : ">", x + w - (module.isToggleable() ? 52 : 20), y + 12, Colors.argb(Theme.TEXT_DIM, a));
        }

        if (vis > 1) {
            ctx.enableScissor(x, y + HEADER, x + w, y + HEADER + vis);
            ctx.fill(x + 10, y + HEADER, x + w - 10, y + HEADER + 1, Colors.argb(0xFFFFFF, 0.06f * a));
            int wy = y + HEADER + 4;
            for (SettingWidget sw : widgets) { sw.render(ctx, x + 12, wy, w - 24, mx, my, a); wy += sw.height(); }
            ctx.disableScissor();
        }
        return total;
    }

    public boolean mouseClicked(double mx, double my, int b) {
        if (mx < x || mx >= x + w || my < y || my >= y + total) return false;
        if (my < y + HEADER) {
            if (b == 0 && module.isToggleable()) module.toggle();
            else if ((b == 1 || b == 0) && !widgets.isEmpty()) expanded = !expanded;
            return true;
        }
        if (expanded && expand.get() > 0.95) {
            for (SettingWidget sw : widgets) if (sw.mouseClicked(mx, my, b)) return true;
        }
        return true;
    }

    public void mouseDragged(double mx, double my) { for (SettingWidget sw : widgets) sw.mouseDragged(mx, my); }
    public void mouseReleased() { for (SettingWidget sw : widgets) sw.mouseReleased(); }
}
