package dev.lumen.render;

import dev.lumen.util.Colors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Small 2D drawing toolkit on top of DrawContext. Minecraft has no rounded-rect primitive,
 * so corners are built from 1px scanlines (cheap at the small radii used here).
 */
public final class Draw {
    private Draw() {}

    private static TextRenderer font() { return MinecraftClient.getInstance().textRenderer; }

    public static void roundedRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if ((color >>> 24) == 0 || w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) { ctx.fill(x, y, x + w, y + h, color); return; }
        ctx.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = r - (int) Math.round(Math.sqrt(r * r - dy * dy));
            ctx.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            ctx.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    /** Soft drop shadow made of a few stacked, very faint rounded rects. */
    public static void shadow(DrawContext ctx, int x, int y, int w, int h, int r, float alpha) {
        for (int i = 1; i <= 6; i++) {
            roundedRect(ctx, x - i, y - i + 2, w + i * 2, h + i * 2, r + i, Colors.argb(0x000000, alpha * 0.03f));
        }
    }

    public static void outline(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x, y, x + w, y + 1, color);
        ctx.fill(x, y + h - 1, x + w, y + h, color);
        ctx.fill(x, y, x + 1, y + h, color);
        ctx.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void text(DrawContext ctx, String s, int x, int y, int color) { text(ctx, s, x, y, color, false); }

    public static void text(DrawContext ctx, String s, int x, int y, int color, boolean shadow) {
        // Vanilla treats very low alpha as fully opaque, so skip near-invisible text instead.
        if ((color >>> 24) < 8) return;
        ctx.drawText(font(), s, x, y, color, shadow);
    }

    public static void text(DrawContext ctx, Text t, int x, int y, int color) {
        if ((color >>> 24) < 8) return;
        ctx.drawText(font(), t, x, y, color, false);
    }

    public static void centeredText(DrawContext ctx, String s, int cx, int y, int color) {
        text(ctx, s, cx - width(s) / 2, y, color);
    }

    public static int width(String s) { return font().getWidth(s); }

    /** iOS-style switch. t = 0..1 animation progress. */
    public static void toggle(DrawContext ctx, int x, int y, int w, int h, float t, float a) {
        int track = Colors.lerp(Colors.argb(0xFFFFFF, 0.12f * a), Colors.argb(Theme.accent(), 0.9f * a), t);
        roundedRect(ctx, x, y, w, h, h / 2, track);
        int k = h - 4;
        int kx = x + 2 + Math.round((w - k - 4) * t);
        roundedRect(ctx, kx, y + 2, k, k, k / 2, Colors.argb(0xFFFFFF, a));
    }
}
