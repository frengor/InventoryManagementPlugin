package com.fren_gor.invManagementPlugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryManagementPluginShaded {

    private GuiListener guiListener;

    public void enable(@NotNull Plugin plugin) {
        guiListener = new GuiListener(plugin);
    }

    public void disable() {
        guiListener.unregister();
    }

}
