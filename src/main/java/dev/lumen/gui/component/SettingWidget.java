package dev.lumen.gui.component;

import net.minecraft.client.gui.DrawContext;

/** One row inside an expanded module card. Position is cached on render so clicks can hit-test later. */
public abstract class SettingWidget {
    protected int x, y, w;

    public abstract int height();
    public abstract void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a);

    public boolean mouseClicked(double mx, double my, int button) { return false; }
    public void mouseDragged(double mx, double my) {}
    public void mouseReleased() {}

    protected void place(int x, int y, int w) { this.x = x; this.y = y; this.w = w; }
    protected boolean hit(double mx, double my) { return mx >= x && mx < x + w && my >= y && my < y + height(); }
}
