package com.fren_gor.invManagementPlugin.guis;

import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EmptyGui extends OpenableInventory implements ClickListener, CloseListener {

    @Getter
    protected final String title;
    protected final int size;

    public EmptyGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size) {
        super(plugin, player);
        Validate.isTrue(size > 0 && size <= 54 && size % 9 == 0, "Invalid size");
        this.title = Objects.requireNonNull(title, "Title is null.");
        this.size = size;
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return Bukkit.createInventory(this, size, title);
    }

    @Override
    public void openInventory() {
        Bukkit.getScheduler().callSyncMethod(plugin, () -> player.openInventory(getInventory()));
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
