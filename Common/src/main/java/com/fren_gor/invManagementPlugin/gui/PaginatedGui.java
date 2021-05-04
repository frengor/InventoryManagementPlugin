package com.fren_gor.invManagementPlugin.gui;

import com.fren_gor.invManagementPlugin.api.Heads;
import com.fren_gor.invManagementPlugin.api.gui.BlockGuiInteractions;
import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaginatedGui implements InventoryHolder, BlockGuiInteractions, ClickListener, CloseListener {

    public static final int ITEMS_PER_PAGE = 45;
    private static final ItemStack GRAY_PANEL = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    private static final ItemStack BARRIER = new ItemStack(Material.BARRIER);
    private static final ItemStack LEFT_ARROW = Heads.ARROW_LEFT.getItem(), RIGHT_ARROW = Heads.ARROW_RIGHT.getItem();

    static {
        ItemMeta m = GRAY_PANEL.getItemMeta();
        m.setDisplayName("§r");
        GRAY_PANEL.setItemMeta(m);

        ItemMeta b = BARRIER.getItemMeta();
        b.setDisplayName("§9Close");
        BARRIER.setItemMeta(b);

        ItemMeta l = LEFT_ARROW.getItemMeta();
        l.setDisplayName("§9Previous Page");
        LEFT_ARROW.setItemMeta(l);

        ItemMeta r = RIGHT_ARROW.getItemMeta();
        r.setDisplayName("§9Next Page");
        RIGHT_ARROW.setItemMeta(r);
    }

    @Getter
    protected final Plugin plugin;
    @Getter
    protected final Player player;
    @Getter
    protected final String title;
    protected final ArrayList<ItemStack> items;
    protected final int page;

    public PaginatedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, ItemStack... items) {
        this.plugin = validatePlugin(plugin);
        this.player = Objects.requireNonNull(player);
        this.title = Objects.requireNonNull(title);
        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = new ArrayList<>(items.length);
            for (ItemStack it : items) {
                if (it != null && it.getType() != Material.AIR)
                    this.items.add(it.clone());
            }
        }
        page = 1;
        Bukkit.getScheduler().callSyncMethod(plugin, () -> player.openInventory(getInventory()));
    }

    public PaginatedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, List<ItemStack> items) {
        this.plugin = validatePlugin(plugin);
        this.player = Objects.requireNonNull(player);
        this.title = Objects.requireNonNull(title);
        if (items == null) {
            this.items = new ArrayList<>();
        } else {
            this.items = new ArrayList<>(items.size());
            for (ItemStack it : items) {
                if (it != null && it.getType() != Material.AIR)
                    this.items.add(it.clone());
            }
        }
        page = 1;
        Bukkit.getScheduler().callSyncMethod(plugin, () -> player.openInventory(getInventory()));
    }

    protected PaginatedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, @NotNull ArrayList<ItemStack> items, int page) {
        this.plugin = validatePlugin(plugin);
        this.player = Objects.requireNonNull(player);
        this.title = Objects.requireNonNull(title);
        this.items = Objects.requireNonNull(items);
        if (page < 1 || items.size() < ITEMS_PER_PAGE)
            this.page = 1;
        else
            this.page = page;
        player.openInventory(getInventory());
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, title);

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

    @NotNull
    private static Plugin validatePlugin(Plugin plugin) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        return plugin;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent event) {
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> arr = new ArrayList<>(items.size());
        for (ItemStack it : items) {
            arr.add(it.clone());
        }
        return arr;
    }

    @Range(from = 1, to = Integer.MAX_VALUE)
    public int getCurrentPage() {
        return page;
    }
}
