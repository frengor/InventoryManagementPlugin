package com.fren_gor.invManagementPlugin.api;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class that provides some methods to interact safely with player inventories.
 */
@UtilityClass
public class SafeInventoryActions {

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
        return CommonSafeInventoryActions.addItem(inventory, item);
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
        return CommonSafeInventoryActions.addItems(inventory, items);
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
        return CommonSafeInventoryActions.addItems(inventory, items);
    }

}
