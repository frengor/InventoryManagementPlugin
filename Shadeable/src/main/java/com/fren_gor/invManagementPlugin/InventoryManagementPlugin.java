package com.fren_gor.invManagementPlugin;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryManagementPlugin {

    private GuiListener guiListener;

    public void onEnable(@NotNull Plugin plugin) {
        guiListener = new GuiListener(plugin);
    }

    public void onDisable() {
        guiListener.unregister();
    }

}
