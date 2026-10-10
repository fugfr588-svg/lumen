package dev.lumen.event;

import dev.lumen.gui.HudEditorScreen;
import dev.lumen.input.CpsTracker;
import dev.lumen.module.HudModule;
import dev.lumen.module.ModuleManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

/** Single HUD element that draws every enabled HudModule. */
public final class HudRenderer implements HudElement {
    @Override
    public void render(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        CpsTracker.poll(mc);
        if (mc.player == null || mc.options.hudHidden) return;
        if (mc.currentScreen instanceof HudEditorScreen) return; // the editor draws them itself
        for (HudModule m : ModuleManager.hudModules()) if (m.isEnabled()) m.render(ctx, false);
    }
}
