package dev.lumen.module;

import com.google.gson.JsonObject;
import dev.lumen.module.setting.BooleanSetting;
import dev.lumen.module.setting.NumberSetting;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.DrawContext;

/**
 * A module that draws something on the in-game HUD at a user-defined position.
 * Positions are edited with the HUD Editor screen and persisted by the config manager.
 */
public abstract class HudModule extends Module {
    protected final BooleanSetting background = add(new BooleanSetting("Background", true));
    protected final NumberSetting opacity = add(new NumberSetting("Opacity", 0, 100, 5, 55));

    protected int x, y, width = 60, height = 16;

    protected HudModule(String name, String description, Category category, int x, int y) {
        super(name, description, category);
        this.x = x; this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }
    public int width() { return width; }
    public int height() { return height; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }

    /** @param editing true when drawn by the HUD editor (draw placeholder data instead of nothing). */
    public final void render(DrawContext ctx, boolean editing) {
        // Keep the element on screen if the window / GUI scale shrank since last time.
        x = Math.max(0, Math.min(x, mc.getWindow().getScaledWidth() - width));
        y = Math.max(0, Math.min(y, mc.getWindow().getScaledHeight() - height));
        renderHud(ctx, editing);
    }

    protected abstract void renderHud(DrawContext ctx, boolean editing);

    protected void size(int w, int h) { width = w; height = h; }

    protected void box(DrawContext ctx) {
        if (background.get()) {
            Draw.roundedRect(ctx, x, y, width, height, 4, Colors.argb(Theme.HUD_BG, opacity.getFloat() / 100f));
        }
    }

    /** Standard "LABEL value" pill used by the simple counters. */
    protected void labelValue(DrawContext ctx, String label, String value) {
        int pad = 5;
        size(pad * 2 + Draw.width(label) + 4 + Draw.width(value), 17);
        box(ctx);
        boolean sh = !background.get();
        Draw.text(ctx, label, x + pad, y + 5, Colors.opaque(Theme.accent()), sh);
        Draw.text(ctx, value, x + pad + Draw.width(label) + 4, y + 5, Colors.opaque(Theme.TEXT), sh);
    }

    @Override public void saveExtra(JsonObject o) { o.addProperty("x", x); o.addProperty("y", y); }
    @Override public void loadExtra(JsonObject o) {
        if (o.has("x")) x = o.get("x").getAsInt();
        if (o.has("y")) y = o.get("y").getAsInt();
    }
}
