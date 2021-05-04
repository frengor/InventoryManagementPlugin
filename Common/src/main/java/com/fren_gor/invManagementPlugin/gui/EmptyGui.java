package com.fren_gor.invManagementPlugin.gui;

import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EmptyGui implements InventoryHolder, ClickListener, CloseListener {

    @Getter
    protected final Plugin plugin;
    @Getter
    protected final Player player;
    @Getter
    protected final String title;
    protected final int size;

    public EmptyGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        Validate.isTrue(size > 0 && size <= 54 && size % 9 == 0, "Invalid size");
        this.plugin = plugin;
        this.title = Objects.requireNonNull(title);
        this.player = Objects.requireNonNull(player);
        this.size = size;
        Bukkit.getScheduler().callSyncMethod(plugin, () -> player.openInventory(getInventory()));
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return Bukkit.createInventory(this, size, title);
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    public int getSize() {
        return size;
    }

}
