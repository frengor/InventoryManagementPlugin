package com.fren_gor.invManagementPlugin.gui;

import com.fren_gor.invManagementPlugin.api.Heads;
import lombok.Getter;
import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import com.fren_gor.invManagementPlugin.util.serializable.Items;
import com.fren_gor.invManagementPlugin.api.InvResult;
import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import com.fren_gor.invManagementPlugin.api.gui.BlockGuiInteractions;
import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClaimGui implements InventoryHolder, BlockGuiInteractions, ClickListener {

    private static final int ITEMS_PER_PAGE = 45;
    private static final ItemStack GRAY_PANEL = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private static final ItemStack BARRIER = new ItemStack(Material.BARRIER);
    private static final ItemStack LEFT_ARROW = Heads.ARROW_LEFT.getItem(), RIGHT_ARROW = Heads.ARROW_RIGHT.getItem();

    static {
        ItemMeta m = GRAY_PANEL.getItemMeta();
        m.setDisplayName("§r");
        GRAY_PANEL.setItemMeta(m);

        ItemMeta b = BARRIER.getItemMeta();
        b.setDisplayName("§9Close");
        b.setLore(Arrays.asList("", "§7You can still claim", "§7your items later."));
        BARRIER.setItemMeta(b);

        ItemMeta l = LEFT_ARROW.getItemMeta();
        l.setDisplayName("§9Previous Page");
        LEFT_ARROW.setItemMeta(l);

        ItemMeta r = RIGHT_ARROW.getItemMeta();
        r.setDisplayName("§9Next Page");
        RIGHT_ARROW.setItemMeta(r);
    }

    @Getter
    private final Player p;
    @Getter
    private final List<ItemStack> items;
    @Getter
    private final int page;

    public ClaimGui(@NotNull Player player) {
        this.p = Objects.requireNonNull(player);
        Items items = InventoryManagementPlugin.getInstance().loadItems(player);
        if (items == null) {
            this.items = Collections.emptyList();
        } else {
            this.items = Objects.requireNonNull(items.getItems());
        }
        page = 1;
        player.openInventory(getInventory());
    }

    private ClaimGui(@NotNull Player player, @NotNull List<ItemStack> items, int page) {
        this.p = player;
        this.items = items;
        if (page < 1 || items.size() < ITEMS_PER_PAGE)
            this.page = 1;
        else
            this.page = page;
        player.openInventory(getInventory());
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, "");

        int i = 0, n = 0, min = (page - 1) * ITEMS_PER_PAGE;
        for (ItemStack it : items) {
            if (i < min) {
                i++;
                continue;
            }
            if (n >= 45) {
                break;
            }
            inv.setItem(n, it.clone());
            n++;
        }

        for (n = 46; n < 53; n++) {
            inv.setItem(n, GRAY_PANEL.clone());
        }

        inv.setItem(49, BARRIER.clone());

        if (page > 1) {
            // Set 45 slot
            inv.setItem(45, LEFT_ARROW.clone());
        } else {
            inv.setItem(45, GRAY_PANEL.clone());
        }
        if (items.size() > min + ITEMS_PER_PAGE) {
            // Set 53 slot
            inv.setItem(53, RIGHT_ARROW.clone());
        } else {
            inv.setItem(53, GRAY_PANEL.clone());
        }
        return inv;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {

        int slot = e.getSlot();

        if (slot > 44) {
            switch (slot) {
                case 45:
                    if (page > 1)
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                new ClaimGui((Player) e.getWhoClicked(), items, page - 1);
                            }
                        }.runTask(InventoryManagementPlugin.getInstance());
                    break;
                case 49:
                    p.closeInventory();
                    break;
                case 53:
                    if (items.size() > page * ITEMS_PER_PAGE)
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                new ClaimGui((Player) e.getWhoClicked(), items, page + 1);
                            }
                        }.runTask(InventoryManagementPlugin.getInstance());
                    break;
            }
            return;
        }

        int index = (page - 1) * ITEMS_PER_PAGE + slot;

        if (index >= items.size()) {
            return;
        }

        ItemStack it = items.remove(index);

        InvResult res = SafeInventoryActions.addItem(p.getInventory(), it);

        if (res == InvResult.MODIFIED) {
            // p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1, 1);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            if (items.size() == 0) {
                InventoryManagementPlugin.getInstance().remove(p);
                p.sendMessage(InventoryManagementPlugin.getInstance().getClaimitemsAllItemsClaimed());
                p.closeInventory();
                return;
            }
            InventoryManagementPlugin.getInstance().save(new Items(p.getUniqueId(), items));
            new BukkitRunnable() {
                @Override
                public void run() {
                    new ClaimGui((Player) e.getWhoClicked(), items, page);
                }
            }.runTask(InventoryManagementPlugin.getInstance());
        } else {
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1, 1);
            if (res == InvResult.NOT_ENOUGH_SPACE) {
                p.sendMessage("§cYou don't have enough space in your inventory.");
            } else {
                p.sendMessage("§cCouldn't add the item to your inventory.");
            }

        }
    }
}
