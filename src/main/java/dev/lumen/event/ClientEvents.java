package dev.lumen.event;

import dev.lumen.config.ConfigManager;
import dev.lumen.input.InputHandler;
import dev.lumen.module.ModuleManager;
import dev.lumen.module.impl.ComboModule;
import dev.lumen.module.impl.ReachModule;
import dev.lumen.module.impl.TargetHudModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

/** Every hook into Minecraft / Fabric lives here and just forwards to the right subsystem. */
public final class ClientEvents {
    private ClientEvents() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            InputHandler.tick(mc);
            if (mc.player != null) ModuleManager.tick();
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
                Identifier.of("lumen", "hud"), new HudRenderer());

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient()) {
                TargetHudModule.onAttack(entity);
                ComboModule.onAttack();
                ReachModule.onAttack(player, hit);
            }
            return ActionResult.PASS;
        });

        ClientLifecycleEvents.CLIENT_STOPPING.register(mc -> ConfigManager.save());
    }
}
