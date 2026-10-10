package dev.lumen.gui;

import dev.lumen.config.ConfigManager;
import dev.lumen.gui.component.KeybindWidget;
import dev.lumen.gui.component.ModuleCard;
import dev.lumen.input.InputHandler;
import dev.lumen.module.Category;
import dev.lumen.module.Module;
import dev.lumen.module.ModuleManager;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Animation;
import dev.lumen.util.Colors;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.util.EnumMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * The main Click GUI: floating, draggable panel with a category sidebar on the left and an
 * animated, scrollable list of module cards on the right.
 */
public final class ClickGuiScreen extends Screen {
    private static final int W = 470, H = 310, SIDEBAR = 108, GAP = 4, NAV_ROW = 24;

    private final Animation open = new Animation(0, 16);
    private final Animation pill = new Animation(0, 20);
    private final Animation scroll = new Animation(0, 18);
    private final Animation[] navHover = new Animation[Category.values().length];
    private final Animation editorHover = new Animation(0, 20);
    private final Map<Category, List<ModuleCard>> cards = new EnumMap<>(Category.class);

    private Category selected = Category.COMBAT;
    private boolean closing, pillInit;
    private double scrollTarget;
    private int maxScroll;
    private int pw, ph;
    private static int panelX = -1, panelY = -1; // remembered between openings
    private boolean draggingPanel;
    private int dragDX, dragDY;
    private KeybindWidget listening;
    // cached layout (set during render, used by input handlers)
    private int sideX, sideY, contentX, contentW, viewTop, viewBottom;

    public ClickGuiScreen() {
        super(Text.literal("Lumen"));
        open.setTarget(1);
        for (int i = 0; i < navHover.length; i++) navHover[i] = new Animation(0, 20);
        for (Category c : Category.values()) {
            List<ModuleCard> list = new ArrayList<>();
            for (Module m : ModuleManager.byCategory(c)) list.add(new ModuleCard(m, w -> listening = w));
            cards.put(c, list);
        }
    }

    @Override
    protected void init() {
        pw = Math.min(W, width - 12);
        ph = Math.min(H, height - 12);
        if (panelX < 0) { panelX = (width - pw) / 2; panelY = (height - ph) / 2; }
        panelX = Math.max(0, Math.min(panelX, width - pw));
        panelY = Math.max(0, Math.min(panelY, height - ph));
    }

    @Override public boolean shouldPause() { return false; }
    @Override public boolean shouldCloseOnEsc() { return false; } // we close with our own animation
    @Override public void close() { beginClose(); }
    @Override public void removed() { ConfigManager.save(); }

    /** Vanilla's background blur, if enabled in Client > Interface. The dim layer is drawn in render(). */
    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        if (ModuleManager.iface().blur.get() && !closing) super.renderBackground(ctx, mx, my, delta);
    }

    private void beginClose() { closing = true; open.setTarget(0); }

    @Override
    public void tick() {
        if (closing && open.get() < 0.03) client.setScreen(null);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        float a = (float) open.get();
        ctx.fill(0, 0, width, height, Colors.argb(0x000000, 0.40f * a));

        int px = panelX, py = panelY + (int) ((1 - a) * 14); // slide up while fading in
        int accent = Theme.accent();
        Draw.shadow(ctx, px, py, pw, ph, 8, a);
        Draw.roundedRect(ctx, px, py, pw, ph, 8, Colors.argb(Theme.PANEL, 0.93f * a));
        ctx.fill(px + 12, py, px + pw - 12, py + 1, Colors.argb(accent, 0.7f * a));

        renderSidebar(ctx, px, py, mx, my, a);
        renderContent(ctx, px, py, mx, my, a);
    }

    private void renderSidebar(DrawContext ctx, int px, int py, int mx, int my, float a) {
        sideX = px + 8; sideY = py + 8;
        int sw = SIDEBAR, sh = ph - 16;
        Draw.roundedRect(ctx, sideX, sideY, sw, sh, 6, Colors.argb(0xFFFFFF, 0.035f * a));
        Draw.text(ctx, Text.literal("LUMEN").formatted(Formatting.BOLD), sideX + 12, sideY + 11, Colors.argb(Theme.accent(), a));
        Draw.text(ctx, "PvP client", sideX + 12, sideY + 23, Colors.argb(Theme.TEXT_DIM, a));

        int navTop = sideY + 42;
        Category[] cats = Category.values();
        double target = navTop + selected.ordinal() * NAV_ROW;
        if (!pillInit) { pill.snap(target); pillInit = true; }
        pill.setTarget(target);
        int pyPill = (int) Math.round(pill.get());
        Draw.roundedRect(ctx, sideX + 6, pyPill, sw - 12, 20, 5, Colors.argb(Theme.accent(), 0.16f * a));
        Draw.roundedRect(ctx, sideX + 6, pyPill + 4, 2, 12, 1, Colors.argb(Theme.accent(), a));

        for (int i = 0; i < cats.length; i++) {
            int ry = navTop + i * NAV_ROW;
            boolean hov = mx >= sideX + 6 && mx < sideX + sw - 6 && my >= ry && my < ry + 20 && !closing;
            navHover[i].setTarget(hov || cats[i] == selected ? 1 : 0);
            int col = Colors.lerp(Colors.opaque(Theme.TEXT_DIM), Colors.opaque(Theme.TEXT), (float) navHover[i].get());
            Draw.text(ctx, cats[i].label(), sideX + 16, ry + 6, Colors.argb(col & 0xFFFFFF, a));
        }

        // HUD editor button at the bottom of the sidebar
        int by = sideY + sh - 26;
        boolean hov = mx >= sideX + 6 && mx < sideX + sw - 6 && my >= by && my < by + 20 && !closing;
        editorHover.setTarget(hov ? 1 : 0);
        Draw.roundedRect(ctx, sideX + 6, by, sw - 12, 20, 5, Colors.argb(0xFFFFFF, (0.05f + 0.06f * (float) editorHover.get()) * a));
        Draw.centeredText(ctx, "HUD Editor", sideX + sw / 2, by + 6, Colors.argb(Theme.TEXT, a));
    }

    private void renderContent(DrawContext ctx, int px, int py, int mx, int my, float a) {
        contentX = sideX + SIDEBAR + 10;
        contentW = px + pw - 8 - contentX;
        int top = py + 8;
        List<ModuleCard> list = cards.get(selected);

        Draw.text(ctx, Text.literal(selected.label()).formatted(Formatting.BOLD), contentX, top + 8, Colors.argb(Theme.TEXT, a));
        String count = list.size() + (list.size() == 1 ? " module" : " modules");
        Draw.text(ctx, count, contentX + contentW - Draw.width(count), top + 8, Colors.argb(Theme.TEXT_DIM, a));
        Draw.text(ctx, "Left-click: toggle   Right-click: settings", contentX, py + ph - 8 - 10, Colors.argb(Theme.TEXT_DIM, a * 0.7f));

        viewTop = top + 28;
        viewBottom = py + ph - 8 - 16;
        scroll.setTarget(scrollTarget);
        int yy = viewTop - (int) Math.round(scroll.get());
        int total = 0;
        boolean inView = my >= viewTop && my < viewBottom && !closing;
        ctx.enableScissor(contentX - 2, viewTop, contentX + contentW + 2, viewBottom);
        for (ModuleCard c : list) {
            int h = c.render(ctx, contentX, yy, contentW, mx, my, a, inView);
            yy += h + GAP; total += h + GAP;
        }
        ctx.disableScissor();

        maxScroll = Math.max(0, total - (viewBottom - viewTop));
        scrollTarget = Math.max(0, Math.min(scrollTarget, maxScroll));
        if (maxScroll > 0) { // slim scrollbar
            int trackH = viewBottom - viewTop;
            int barH = Math.max(16, trackH * trackH / total);
            int barY = viewTop + (int) ((trackH - barH) * (scroll.get() / maxScroll));
            Draw.roundedRect(ctx, contentX + contentW + 3, barY, 2, barH, 1, Colors.argb(Theme.accent(), 0.6f * a));
        }
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (closing) return true;
        double mx = click.x(), my = click.y();
        int b = click.button();

        if (listening != null) { listening.setListening(false); listening = null; return true; }

        // drag the panel by its top strip
        if (b == 0 && my >= panelY && my < panelY + 34 && mx >= panelX && mx < panelX + pw) {
            draggingPanel = true; dragDX = (int) mx - panelX; dragDY = (int) my - panelY;
            return true;
        }

        if (b == 0) {
            Category[] cats = Category.values();
            int navTop = sideY + 42;
            for (int i = 0; i < cats.length; i++) {
                int ry = navTop + i * NAV_ROW;
                if (mx >= sideX + 6 && mx < sideX + SIDEBAR - 6 && my >= ry && my < ry + 20) {
                    if (selected != cats[i]) { selected = cats[i]; scrollTarget = 0; scroll.snap(0); }
                    return true;
                }
            }
            int by = sideY + ph - 16 - 26;
            if (mx >= sideX + 6 && mx < sideX + SIDEBAR - 6 && my >= by && my < by + 20) {
                client.setScreen(new HudEditorScreen(this));
                return true;
            }
        }

        if (my >= viewTop && my < viewBottom && mx >= contentX) {
            for (ModuleCard c : cards.get(selected)) if (c.mouseClicked(mx, my, b)) return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        if (draggingPanel) {
            panelX = Math.max(0, Math.min((int) click.x() - dragDX, width - pw));
            panelY = Math.max(0, Math.min((int) click.y() - dragDY, height - ph));
            return true;
        }
        for (ModuleCard c : cards.get(selected)) c.mouseDragged(click.x(), click.y());
        return true;
    }

    @Override
    public boolean mouseReleased(Click click) {
        draggingPanel = false;
        for (ModuleCard c : cards.get(selected)) c.mouseReleased();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double horizontal, double vertical) {
        scrollTarget = Math.max(0, Math.min(scrollTarget - vertical * 28, maxScroll));
        return true;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int key = input.key();
        if (listening != null) {
            if (key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE) listening.module().setKey(-1);
            else if (key != GLFW.GLFW_KEY_ESCAPE) listening.module().setKey(key);
            listening.setListening(false);
            listening = null;
            return true;
        }
        if (key == GLFW.GLFW_KEY_ESCAPE || key == InputHandler.openGuiKeyCode()) { beginClose(); return true; }
        return super.keyPressed(input);
    }
}
