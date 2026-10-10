package dev.lumen.module.impl;

import dev.lumen.module.Category;
import dev.lumen.module.Module;
import dev.lumen.module.setting.BooleanSetting;
import dev.lumen.module.setting.NumberSetting;

/** Always-on module that only exists to expose the client's appearance settings in the GUI. */
public final class InterfaceModule extends Module {
    public final NumberSetting hue = add(new NumberSetting("Accent Hue", 0, 360, 1, 210));
    public final NumberSetting saturation = add(new NumberSetting("Accent Saturation", 0, 100, 1, 65));
    public final BooleanSetting blur = add(new BooleanSetting("Background Blur", true));
    public final BooleanSetting animations = add(new BooleanSetting("Animations", true));

    public InterfaceModule() { super("Interface", "Accent colour, blur and animations", Category.CLIENT); }

    @Override public boolean isToggleable() { return false; }
}
