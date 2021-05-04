package com.fren_gor.invManagementPlugin.api;

import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import com.fren_gor.invManagementPlugin.util.ReflectionUtil;
import com.fren_gor.invManagementPlugin.util.serializable.Items;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
     * Adds items to the player inventory. If not enough space is found, adds the items to the unclaimed player items.
     * <p>Sends the "you have unclaimed items" message if inventory is full.
     *
     * @param player The player to add the items to.
     * @param items The items to add.
     * @return Whether every item has been added.
     */
    public static boolean partialAdd(@NotNull Player player, @NotNull ItemStack... items) {
        return partialAdd(player, true, items);
    }

    /**
     * Adds items to the player inventory. If not enough space is found, adds the items to the unclaimed player items.
     *
     * @param player The player to add the items to.
     * @param sendMessage Whether to send the "you have unclaimed items" message if inventory is full.
     * @param items The items to add.
     * @return Whether every item has been added.
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
