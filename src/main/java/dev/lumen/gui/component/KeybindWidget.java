package dev.lumen.gui.component;

import dev.lumen.module.Module;
import dev.lumen.render.Draw;
import dev.lumen.render.Theme;
import dev.lumen.util.Colors;
import dev.lumen.util.KeyNames;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

/** Click, then press a key to bind (Esc cancels, Backspace/Delete clears). The screen captures the key press. */
public final class KeybindWidget extends SettingWidget {
    private final Module module;
    private final Consumer<KeybindWidget> onListen;
    private boolean listening;

    public KeybindWidget(Module m, Consumer<KeybindWidget> onListen) { this.module = m; this.onListen = onListen; }

    public Module module() { return module; }
    public void setListening(boolean l) { listening = l; }

    @Override public int height() { return 20; }

    @Override public void render(DrawContext ctx, int x, int y, int w, int mx, int my, float a) {
        place(x, y, w);
        Draw.text(ctx, "Keybind", x, y + 6, Colors.argb(Theme.TEXT, a));
        String v = listening ? "..." : KeyNames.get(module.key());
        int pw = Draw.width(v) + 12;
        int bg = listening ? Colors.argb(Theme.accent(), 0.25f * a) : Colors.argb(0xFFFFFF, 0.08f * a);
        Draw.roundedRect(ctx, x + w - pw, y + 3, pw, 14, 4, bg);
        Draw.text(ctx, v, x + w - pw + 6, y + 6, Colors.argb(listening ? Theme.accent() : Theme.TEXT_DIM, a));
    }

    @Override public boolean mouseClicked(double mx, double my, int b) {
        if (b == 0 && hit(mx, my)) { listening = true; onListen.accept(this); return true; }
        return false;
    }
}
