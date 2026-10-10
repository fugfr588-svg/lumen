package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public final class PingModule extends HudModule {
    public PingModule() { super("Ping", "Shows your latency to the server", Category.HUD, 6, 26); }

    @Override protected void renderHud(DrawContext ctx, boolean editing) {
        int ping = 0;
        if (mc.player != null && mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) ping = e.getLatency();
        }
        labelValue(ctx, "PING", ping + " ms");
    }
}
