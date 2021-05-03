package com.fren_gor.invManagementPlugin.gui;

import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class EmptyGui implements InventoryHolder, ClickListener, CloseListener {

    @Getter
    @NotNull
    private final Player player;
    @Getter
    @NotNull
    private final String title;
    @Getter
    @Range(from = 9, to = 54)
    private final int size;
    @Getter
    @Nullable
    private final Consumer<InventoryCloseEvent> onClose;

    public EmptyGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size) {
        this(plugin, player, title, size, null);
    }

    public EmptyGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size, @Nullable Consumer<InventoryCloseEvent> onClose) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        Validate.isTrue(size > 0 && size <= 54 && size % 9 == 0, "Invalid size");
        this.title = Objects.requireNonNull(title);
        this.player = Objects.requireNonNull(player);
        this.onClose = onClose;
        this.size = size;
        Bukkit.getScheduler().callSyncMethod(plugin, () -> player.openInventory(getInventory()));
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return Bukkit.createInventory(this, size, title);
    }

}
