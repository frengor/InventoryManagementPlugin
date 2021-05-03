package com.fren_gor.invManagementPlugin.api;

import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import com.fren_gor.invManagementPlugin.util.ReflectionUtil;
import com.fren_gor.invManagementPlugin.util.serializable.Items;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Class that provides some methods to interact safely with player inventories.
 */
@UtilityClass
public class SafeInventoryActions {

    private static Method asCraftCopy;

    static {
        final Class<?> craftItemStack = ReflectionUtil.getCBClass("inventory.CraftItemStack");
        try {
            asCraftCopy = craftItemStack.getDeclaredMethod("asCraftCopy", ItemStack.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add items to the player inventory. If not enough space is found, adds the items to the unclaimed player items.<br>
     * This sends the "you have unclaimed items" message if inventory is full.
     *
     * @param player The player to add the items to.
     * @param items The items to add.
     * @return Whether every items has been added.
     */
    public static boolean partialAdd(@NotNull Player player, @NotNull ItemStack... items) {
        return partialAdd(player, true, items);
    }

    /**
     * Add items to the player inventory. If not enough space is found, adds the items to the unclaimed player items.
     *
     * @param player The player to add the items to.
     * @param sendMessage Whether to send the "you have unclaimed items" message if inventory is full.
     * @param items The items to add.
     * @return Whether every items has been added.
     */
    public static boolean partialAdd(@NotNull Player player, boolean sendMessage, @NotNull ItemStack... items) {
        Validate.notNull(player, "Player cannot be null.");
        Validate.notNull(items, "Items cannot be null.");
        Map<Integer, ItemStack> map = player.getInventory().addItem(items);
        if (map.size() == 0) {
            return true;
        }
        InventoryManagementPlugin instance = InventoryManagementPlugin.getInstance();
        Items savedItems;
        if (instance.hasItems(player)) {
            savedItems = instance.loadItems(player);
            if (savedItems == null) {
                throw new RuntimeException("savedItems is null.");
            }
        } else {
            savedItems = new Items(player.getUniqueId());
        }
        List<ItemStack> list = savedItems.getItems();
        for (ItemStack it : map.values()) {
            list.add(it.clone());
        }
        instance.addAndSave(player, savedItems);
        if (sendMessage) {
            player.sendMessage(instance.getFgiveNoFreeSpace());
        }
        return false;
    }

    /**
     * Try to add an item to an inventory.<br>
     * <br>
     * Modified from CraftBukkit.
     *
     * @param inventory The source inventory
     * @param item The item to add
     * @return If the inventory has been modified.
     */
    @NotNull
    public static InvResult addItem(@NotNull Inventory inventory, ItemStack item) {
        Validate.notNull(inventory, "Inventory cannot be null.");
        ItemStack[] oldInv = inventory.getStorageContents();
        if (item == null || item.getType() == Material.AIR) {
            return InvResult.NOT_MODIFIED;
        }
        ItemStack[] inv = new ItemStack[oldInv.length];
        for (int i = 0; i < inv.length; i++) {
            if (oldInv[i] != null)
                inv[i] = oldInv[i].clone();
        }
        int maxStack = inventory.getMaxStackSize();
        item = item.clone();
        while (true) {
            // Do we already have a stack of it?
            int firstPartial = firstPartial(inv, item);

            // Drat! no partial stack
            if (firstPartial == -1) {
                // Find a free spot!
                int firstFree = firstEmpty(inv);

                if (firstFree == -1) {
                    // No space at all!
                    return InvResult.NOT_ENOUGH_SPACE;
                } else {
                    // More than a single stack!
                    if (item.getAmount() > maxStack) {
                        ItemStack stack;
                        try {
                            stack = (ItemStack) asCraftCopy.invoke(null, item);
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                            return InvResult.NOT_MODIFIED;
                        }
                        stack.setAmount(maxStack);
                        inv[firstFree] = stack;
                        item.setAmount(item.getAmount() - maxStack);
                    } else {
                        // Just store it
                        inv[firstFree] = item;
                        break;
                    }
                }
            } else {
                // So, apparently it might only partially fit, well lets do
                // just that
                ItemStack partialItem = inv[firstPartial];

                int amount = item.getAmount();
                int partialAmount = partialItem.getAmount();
                int maxAmount = partialItem.getMaxStackSize();

                // Check if it fully fits
                if (amount + partialAmount <= maxAmount) {
                    partialItem.setAmount(amount + partialAmount);
                    // To make sure the packet is sent to the client
                    inv[firstPartial] = partialItem;
                    break;
                }

                // It fits partially
                partialItem.setAmount(maxAmount);
                // To make sure the packet is sent to the client
                inv[firstPartial] = partialItem;
                item.setAmount(amount + partialAmount - maxAmount);
            }
        }

        inventory.setStorageContents(inv);
        return InvResult.MODIFIED;
    }

    /**
     * Try to add items to an inventory<br>
     * <br>
     * Modified from CraftBukkit.
     *
     * @param inventory The source inventory
     * @param items The items to add
     * @return The modified inventory. null if there isn't enough space in inventory for the items.
     */
    @NotNull
    public static InvResult addItem(@NotNull Inventory inventory, List<ItemStack> items) {
        Validate.notNull(inventory, "Inventory cannot be null.");
        ItemStack[] oldInv = inventory.getStorageContents();
        if (items == null || items.size() == 0) {
            // No item is added
            return InvResult.NOT_MODIFIED;
        }
        ItemStack[] inv = new ItemStack[oldInv.length];
        for (int i = 0; i < inv.length; i++) {
            if (oldInv[i] != null)
                inv[i] = oldInv[i].clone();
        }
        int maxStack = inventory.getMaxStackSize();
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(inv, item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty(inv);

                    if (firstFree == -1) {
                        // No space at all!
                        return InvResult.NOT_ENOUGH_SPACE;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > maxStack) {
                            ItemStack stack;
                            try {
                                stack = (ItemStack) asCraftCopy.invoke(null, item);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                return InvResult.NOT_MODIFIED;
                            }
                            stack.setAmount(maxStack);
                            inv[firstFree] = stack;
                            item.setAmount(item.getAmount() - maxStack);
                        } else {
                            // Just store it
                            inv[firstFree] = item;
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do
                    // just that
                    ItemStack partialItem = inv[firstPartial];

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        // To make sure the packet is sent to the client
                        inv[firstPartial] = partialItem;
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    // To make sure the packet is sent to the client
                    inv[firstPartial] = partialItem;
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        inventory.setStorageContents(inv);
        return InvResult.MODIFIED;
    }

    private static int firstPartial(ItemStack[] inventory, ItemStack item) {
        /*if (item == null) {
            return -1;
        }*/
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(item)) {
                return i;
            }
        }
        return -1;
    }

    private static int firstEmpty(ItemStack[] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

}
