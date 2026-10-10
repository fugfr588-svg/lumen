package dev.lumen.input;

import dev.lumen.gui.ClickGuiScreen;
import dev.lumen.module.Module;
import dev.lumen.module.ModuleManager;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

/**
 * Two kinds of keys: the Click GUI key (a real vanilla KeyBinding, so it shows in Controls and is
 * rebindable there; default Right Shift) and per-module toggle keys (polled from GLFW).
 */
public final class InputHandler {
    private InputHandler() {}

    public static KeyBinding openGui;
    private static final Map<Module, Boolean> DOWN = new HashMap<>();

    public static void init() {
        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("lumen", "main"));
        openGui = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.lumen.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, cat));
    }

    /** GLFW code the open-GUI key is currently bound to (used to close the GUI with the same key). */
    public static int openGuiKeyCode() { return KeyBindingHelper.getBoundKeyOf(openGui).getCode(); }

    public static void tick(MinecraftClient mc) {
        while (openGui.wasPressed()) {
            if (mc.player != null && mc.currentScreen == null) mc.setScreen(new ClickGuiScreen());
        }
        if (mc.player == null || mc.currentScreen != null) { DOWN.clear(); return; }
        long handle = mc.getWindow().getHandle();
        for (Module m : ModuleManager.all()) {
            if (m.key() < 0 || !m.isToggleable()) continue;
            boolean p = GLFW.glfwGetKey(handle, m.key()) == GLFW.GLFW_PRESS;
            if (p && !DOWN.getOrDefault(m, false)) m.toggle();
            DOWN.put(m, p);
        }
    }
}
