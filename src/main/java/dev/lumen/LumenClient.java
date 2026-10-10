package dev.lumen;

import dev.lumen.config.ConfigManager;
import dev.lumen.event.ClientEvents;
import dev.lumen.input.InputHandler;
import dev.lumen.module.ModuleManager;
import dev.lumen.util.Animation;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LumenClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Lumen");

    @Override
    public void onInitializeClient() {
        ModuleManager.init();                 // 1. create modules
        ConfigManager.load();                 // 2. apply saved settings
        Animation.enabled = () -> ModuleManager.iface().animations.get();
        InputHandler.init();                  // 3. keybinds
        ClientEvents.init();                  // 4. hooks
        LOGGER.info("Lumen loaded with {} modules", ModuleManager.all().size());
    }
}
