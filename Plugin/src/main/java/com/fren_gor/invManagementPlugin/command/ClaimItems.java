package com.fren_gor.invManagementPlugin.command;

import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import com.fren_gor.invManagementPlugin.api.InvResult;
import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import com.fren_gor.invManagementPlugin.guis.PaginatedGui;
import com.fren_gor.invManagementPlugin.util.serializable.Items;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ClaimItems implements CommandExecutor, TabCompleter {

    private final InventoryManagementPlugin instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to perform this command.");
            return false;
        }

        Player p = (Player) sender;

        if (!instance.hasItems(p)) {
            p.sendMessage(instance.getClaimitemsNoItemToClaim());
            return false;
        }

        List<ItemStack> list;
        Items items = InventoryManagementPlugin.getInstance().loadItems(p);
        if (items == null) {
            list = Collections.emptyList();
        } else {
            list = Objects.requireNonNull(items.getItems());
        }

        new ClaimGui(instance, p, "", list);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }

    private static class ClaimGui extends PaginatedGui {

        public ClaimGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, List<ItemStack> items) {
            super(plugin, player, title, items);
        }

        @Override
        public void onClick(@NotNull Inventory inventory, @NotNull InventoryClickEvent e) {
            int index = getItemIndex(e.getSlot());

            if (index == -1) {
                return;
            }

            ItemStack it = items.remove(index);

            InvResult res = SafeInventoryActions.addItem(player.getInventory(), it);

            if (res == InvResult.MODIFIED) {
                // p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                if (items.size() == 0) {
                    InventoryManagementPlugin.getInstance().remove(player);
                    player.sendMessage(InventoryManagementPlugin.getInstance().getClaimitemsAllItemsClaimed());
                    player.closeInventory();
                    return;
                }
                InventoryManagementPlugin.getInstance().save(new Items(player.getUniqueId(), items));
                updateInventory(inventory);
            } else {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
                if (res == InvResult.NOT_ENOUGH_SPACE) {
                    player.sendMessage("§cYou don't have enough space in your inventory.");
                } else {
                    player.sendMessage("§cCouldn't add the item to your inventory.");
                }

            }
        }

    }

}
