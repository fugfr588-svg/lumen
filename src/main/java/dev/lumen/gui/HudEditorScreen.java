package dev.lumen.gui;

import dev.lumen.config.ConfigManager;
import dev.lumen.module.HudModule;
import dev.lumen.module.ModuleManager;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/** Drag HUD elements around. Snaps to screen edges and centre lines. Esc returns to the Click GUI. */
public final class HudEditorScreen extends Screen {
    private final Screen parent;
    private HudModule dragging;
    private int offX, offY;

    public HudEditorScreen(Screen parent) { super(Text.literal("HUD Editor")); this.parent = parent; }

    @Override public boolean shouldPause() { return false; }
    @Override public void renderBackground(DrawContext ctx, int mx, int my, float delta) { /* no blur: we want to see the HUD */ }
    @Override public void close() { client.setScreen(parent); }
    @Override public void removed() { ConfigManager.save(); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, Colors.argb(0x000000, 0.35f));
        for (HudModule m : ModuleManager.hudModules()) {
            if (!m.isEnabled()) continue;
            m.render(ctx, true);
            boolean hov = m == dragging || (dragging == null && inside(m, mx, my));
            Draw.outline(ctx, m.x() - 1, m.y() - 1, m.width() + 2, m.height() + 2,
                    Colors.argb(Theme.accent(), hov ? 1f : 0.45f));
        }
        Draw.centeredText(ctx, "HUD Editor  -  drag elements, Esc to go back", width / 2, 8, Colors.opaque(Theme.TEXT));
    }

    private static boolean inside(HudModule m, double mx, double my) {
        return mx >= m.x() && mx < m.x() + m.width() && my >= m.y() && my < m.y() + m.height();
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() != 0) return false;
        List<HudModule> list = ModuleManager.hudModules();
        for (int i = list.size() - 1; i >= 0; i--) { // topmost first
            HudModule m = list.get(i);
            if (m.isEnabled() && inside(m, click.x(), click.y())) {
                dragging = m; offX = (int) click.x() - m.x(); offY = (int) click.y() - m.y();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        if (dragging == null) return false;
        int nx = (int) click.x() - offX, ny = (int) click.y() - offY, w = dragging.width(), h = dragging.height();
        nx = snap(nx, w, width); ny = snap(ny, h, height);
        dragging.setPosition(nx, ny);
        return true;
    }

    /** Snap a coordinate to the 0 edge, far edge or centre when within 5px. */
    private static int snap(int pos, int size, int screen) {
        if (Math.abs(pos) < 5) return 0;
        if (Math.abs(pos + size - screen) < 5) return screen - size;
        if (Math.abs(pos + size / 2 - screen / 2) < 5) return screen / 2 - size / 2;
        return pos;
    }

    @Override public boolean mouseReleased(Click click) { dragging = null; return true; }
}
