package com.fren_gor.invManagementPlugin.gui;

import lombok.Getter;
import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class EmptyGui implements InventoryHolder, CloseListener {

    @Getter
    @NotNull
    private final Player p;
    @Getter
    @NotNull
    private final String title;
    @Getter
    private final int size;
    @Getter
    @Nullable
    private final Consumer<InventoryCloseEvent> onClose;

    public EmptyGui(@NotNull Player p, @NotNull String title, int size, @Nullable Consumer<InventoryCloseEvent> onClose) {
        Validate.isTrue(size > 0 && size <= 54 && size % 9 == 0, "Invalid size");
        this.title = Objects.requireNonNull(title);
        this.p = Objects.requireNonNull(p);
        this.onClose = onClose;
        this.size = size;
        new BukkitRunnable() {
            @Override
            public void run() {
                p.openInventory(getInventory());
            }
        }.runTask(InventoryManagementPlugin.getInstance());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return Bukkit.createInventory(this, size, title);
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent e) {
        if (onClose != null)
            onClose.accept(e);
    }

    @Override
    public String toString() {
        return "EmptyGui{" +
                "title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmptyGui emptyGui = (EmptyGui) o;

        if (size != emptyGui.size) return false;
        if (!p.equals(emptyGui.p)) return false;
        return title.equals(emptyGui.title);
    }

    @Override
    public int hashCode() {
        int result = p.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + size;
        return result;
    }
}
