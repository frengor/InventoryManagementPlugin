package com.fren_gor.invManagementPlugin.guis;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public abstract class OpenableInventory implements InventoryHolder {

    @Getter
    protected final Plugin plugin;
    @Getter
    protected final Player player;

    public OpenableInventory(@NotNull Plugin plugin, @NotNull Player player) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        Validate.notNull(player, "Player is null.");
        this.plugin = plugin;
        this.player = player;
    }

    public abstract void openInventory();

}
