package com.fren_gor.invManagementPlugin.api;

import com.fren_gor.invManagementPlugin.util.ReflectionUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.List;

@UtilityClass
class CommonSafeInventoryActions {

    private static Method asCraftCopy;

    static {
        final Class<?> craftItemStack = ReflectionUtil.getCBClass("inventory.CraftItemStack");
        assert craftItemStack != null : "Couldn't find class CraftItemStack.";
        try {
            asCraftCopy = craftItemStack.getDeclaredMethod("asCraftCopy", ItemStack.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tries to add items to an inventory.
     *
     * @param inventory The source inventory.
     * @param item Item to add.
     * @return {@link InvResult#MODIFIED} if the item has been added to the inventory,
     * {@link InvResult#NOT_ENOUGH_SPACE} if there wasn't enough space,
     * or {@link InvResult#NOT_MODIFIED} if item couldn't have been added or an error has occurred.
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
        final int maxStack = inventory.getMaxStackSize();
        InvResult result = tryToAddItem(inv, item, maxStack);
        if (result != InvResult.MODIFIED)
            return result;

        inventory.setStorageContents(inv);
        return InvResult.MODIFIED;
    }

    /**
     * Tries to add items to an inventory.
     *
     * @param inventory The source inventory.
     * @param items Items to add.
     * @return {@link InvResult#MODIFIED} if the items has been added to the inventory,
     * {@link InvResult#NOT_ENOUGH_SPACE} if there wasn't enough space,
     * or {@link InvResult#NOT_MODIFIED} if items couldn't have been added or an error has occurred.
     */
    @NotNull
    public static InvResult addItems(@NotNull Inventory inventory, ItemStack... items) {
        Validate.notNull(inventory, "Inventory cannot be null.");
        ItemStack[] oldInv = inventory.getStorageContents();
        if (items == null || items.length == 0) {
            // No item is added
            return InvResult.NOT_MODIFIED;
        }
        ItemStack[] inv = new ItemStack[oldInv.length];
        for (int i = 0; i < inv.length; i++) {
            if (oldInv[i] != null)
                inv[i] = oldInv[i].clone();
        }
        final int maxStack = inventory.getMaxStackSize();
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            InvResult result = tryToAddItem(inv, item, maxStack);
            if (result != InvResult.MODIFIED)
                return result;
        }
        inventory.setStorageContents(inv);
        return InvResult.MODIFIED;
    }

    /**
     * Tries to add items to an inventory.
     *
     * @param inventory The source inventory.
     * @param items Items to add.
     * @return {@link InvResult#MODIFIED} if the items has been added to the inventory,
     * {@link InvResult#NOT_ENOUGH_SPACE} if there wasn't enough space,
     * or {@link InvResult#NOT_MODIFIED} if items couldn't have been added or an error has occurred.
     */
    @NotNull
    public static InvResult addItems(@NotNull Inventory inventory, List<ItemStack> items) {
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
        final int maxStack = inventory.getMaxStackSize();
        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            InvResult result = tryToAddItem(inv, item, maxStack);
            if (result != InvResult.MODIFIED)
                return result;
        }
        inventory.setStorageContents(inv);
        return InvResult.MODIFIED;
    }

    // From CraftBukkit
    private static InvResult tryToAddItem(ItemStack[] inv, ItemStack item, int maxStackSize) {
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
                    if (item.getAmount() > maxStackSize) {
                        ItemStack stack;
                        try {
                            stack = (ItemStack) asCraftCopy.invoke(null, item);
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                            return InvResult.NOT_MODIFIED;
                        }
                        stack.setAmount(maxStackSize);
                        inv[firstFree] = stack;
                        item.setAmount(item.getAmount() - maxStackSize);
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
        return InvResult.MODIFIED;
    }

    // From CraftBukkit
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

    // From CraftBukkit
    private static int firstEmpty(ItemStack[] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] == null) {
                return i;
            }
        }
        return -1;
    }

}
