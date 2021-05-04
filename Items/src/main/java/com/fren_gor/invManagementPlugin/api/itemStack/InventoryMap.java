package com.fren_gor.invManagementPlugin.api.itemStack;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static com.fren_gor.invManagementPlugin.util.ReflectionUtil.VERSION;

public final class InventoryMap extends HashMap<Integer, ItemStack> {

    private static final long serialVersionUID = -2375620583757374598L;

    @Getter
    private final int inventorySize;
    @Getter
    private String title;

    public void setTitle(String title) {
        Validate.isTrue(VERSION > 13, "Title cannot be changed in 1.8-1.13");
        this.title = title;
    }

    InventoryMap() {
        throw new UnsupportedOperationException("Illegal use of constructor");
    }

    InventoryMap(int inventorySize, String title) {
        super();
        this.inventorySize = inventorySize;
        this.title = title;
    }

    InventoryMap(int initialCapacity, float loadFactor, int inventorySize, String title) {
        super(initialCapacity, loadFactor);
        this.inventorySize = inventorySize;
        this.title = title;
    }

    InventoryMap(int initialCapacity, int inventorySize, String title) {
        super(initialCapacity);
        this.inventorySize = inventorySize;
        this.title = title;
    }

    InventoryMap(InventoryMap m, String title) {
        super(m);
        inventorySize = m.getInventorySize();
        this.title = title;
    }

    public Inventory toInventory(InventoryHolder owner) {
        Inventory inv;
        if (title != null)
            inv = Bukkit.createInventory(owner, inventorySize, title);
        else
            inv = Bukkit.createInventory(owner, inventorySize);
        for (Entry<Integer, ItemStack> e : entrySet()) {
            inv.setItem(e.getKey(), e.getValue() == null ? null : e.getValue().clone());
        }
        return inv;
    }

    public ItemStack[] getContents() {
        ItemStack[] arr = new ItemStack[size()];
        int i = 0;
        for (ItemStack it : values()) {
            arr[i++] = it.clone();
        }
        return arr;
    }

    public ItemStack[] getStorageContents() {
        ItemStack[] arr = new ItemStack[inventorySize];
        for (int i = 0; i < inventorySize; i++) {
            ItemStack it = get(i);
            if (it == null) {
                arr[i] = new ItemStack(Material.AIR);
            } else {
                arr[i] = it.clone();
            }
        }
        return arr;
    }

}
