package com.fren_gor.invManagementPlugin.guis;

import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiInteractions;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LockedGui extends EmptyGui implements BlockTopGuiInteractions {

    protected final ItemStack[] initialItems;

    public LockedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size) {
        this(plugin, player, title, size, Collections.emptyList());
    }

    public LockedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size, ItemStack... initialItems) {
        super(plugin, player, title, size);
        Validate.notNull(initialItems, "Initial items array is null.");
        // Validate.isTrue(initialItems.length <= size, "Too many initial items (they exceed the inventory size).");
        int len = Math.min(initialItems.length, size);
        this.initialItems = new ItemStack[len];
        for (int i = 0; i < len; i++) {
            @Nullable ItemStack it = initialItems[i];
            if (it == null) {
                this.initialItems[i] = null;
            } else {
                this.initialItems[i] = it.clone();
            }
        }
    }

    public LockedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size, @NotNull List<@Nullable ItemStack> initialItems) {
        super(plugin, player, title, size);
        Validate.notNull(initialItems, "Initial items list is null.");
        // Validate.isTrue(initialItems.length <= size, "Too many initial items (they exceed the inventory size).");
        int len = Math.min(initialItems.size(), size);
        this.initialItems = new ItemStack[len];
        Iterator<ItemStack> iter = initialItems.listIterator();
        for (int i = 0; i < len; i++) {
            @Nullable ItemStack it = iter.next(); // Doesn't throw errors since list has always an element
            if (it == null) {
                this.initialItems[i] = null;
            } else {
                this.initialItems[i] = it.clone();
            }
        }
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        Inventory inv = super.getInventory();
        for (int i = 0; i < initialItems.length; i++) {
            @Nullable ItemStack it = initialItems[i];
            if (it == null) {
                inv.setItem(i, null);
            } else {
                inv.setItem(i, it.clone());
            }
        }
        return inv;
    }
}
