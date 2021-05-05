package com.fren_gor.invManagementPlugin.guis;

import com.fren_gor.invManagementPlugin.api.Heads;
import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiInteractions;
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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaginatedGui implements InventoryHolder, BlockTopGuiInteractions, ClickListener, CloseListener {

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
    private int page;

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

    @Override
    @NotNull
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 54, title);

        for (int i = 46; i < 53; i++) {
            inv.setItem(i, GRAY_PANEL.clone());
        }

        customizeInventory(inv);

        updateInventory(inv);

        inv.setItem(49, BARRIER.clone());
        return inv;
    }

    @NotNull
    private static Plugin validatePlugin(Plugin plugin) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        return plugin;
    }

    @Override
    public final void onClick(@NotNull InventoryClickEvent e) {
        switch (e.getSlot()) {
            case 45:
                previousPage(e.getClickedInventory());
                break;
            case 49:
                player.closeInventory();
                break;
            case 53:
                nextPage(e.getClickedInventory());
                break;
            default:
                onClick(e.getClickedInventory(), e);
                break;
        }
    }

    public void onClick(@NotNull Inventory inventory, @NotNull InventoryClickEvent event) {
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent event) {
    }

    @Nullable
    public ItemStack getItem(int slot) {
        return getItemInPage(slot, page);
    }

    @Nullable
    public ItemStack getItemInPage(int slot, int page) {
        int i = getItemIndex(slot, page);
        return i == -1 ? null : items.get(i);
    }

    @Range(from = -1, to = Integer.MAX_VALUE)
    public int getItemIndex(int slot) {
        return getItemIndex(slot, page);
    }

    @Range(from = -1, to = Integer.MAX_VALUE)
    public int getItemIndex(int slot, int page) {
        if (page <= 0 || slot < 0 || slot > 44) {
            return -1;
        }
        int i = (page - 1) * ITEMS_PER_PAGE + slot;
        return i >= items.size() ? -1 : i;
    }

    public ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> arr = new ArrayList<>(items.size());
        for (ItemStack it : items) {
            arr.add(it.clone());
        }
        return arr;
    }

    public ArrayList<ItemStack> getPageItems() {
        ArrayList<ItemStack> arr = new ArrayList<>(45);
        int min = getPageItemsMinIndex(), max = Math.min(getPageItemsMaxIndex(), items.size());
        for (int i = min; i < max; i++) {
            arr.add(items.get(i));
        }
        return arr;
    }

    public boolean previousPage(@NotNull Inventory inv) {
        Validate.notNull(inv, "Inventory is null.");
        Validate.isTrue(inv.getHolder() == this, "Invalid inventory.");
        if (page <= 1) {
            return false;
        }
        page--;
        updateInventory(inv);
        return true;
    }

    public boolean nextPage(@NotNull Inventory inv) {
        Validate.notNull(inv, "Inventory is null.");
        Validate.isTrue(inv.getHolder() == this, "Invalid inventory.");
        if (items.size() <= page * ITEMS_PER_PAGE) {
            return false;
        }
        page++;
        updateInventory(inv);
        return true;
    }

    public void updateInventory(@NotNull Inventory inv) {
        Validate.notNull(inv, "Inventory is null.");
        int min = getPageItemsMinIndex(), max = Math.min(getPageItemsMaxIndex(), items.size());
        int n = 0;
        for (int i = min; i < max; n++, i++) {
            inv.setItem(n, items.get(i));
        }
        for (; n < 45; n++) {
            inv.setItem(n, new ItemStack(Material.AIR));
        }
        if (page > 1) {
            // Set 45th slot
            inv.setItem(45, LEFT_ARROW.clone());
        } else {
            inv.setItem(45, GRAY_PANEL.clone());
        }
        if (items.size() > getPageItemsMinIndex() + ITEMS_PER_PAGE) {
            // Set 53th slot
            inv.setItem(53, RIGHT_ARROW.clone());
        } else {
            inv.setItem(53, GRAY_PANEL.clone());
        }
    }

    @Range(from = 1, to = Integer.MAX_VALUE)
    public int getCurrentPage() {
        return page;
    }

    protected int getPageItemsMinIndex() {
        return (page - 1) * ITEMS_PER_PAGE;
    }

    protected int getPageItemsMaxIndex() {
        return (page - 1) * ITEMS_PER_PAGE + 45;
    }

    /**
     * Method to set custom items in slots 46, 47, 48, 50, 51, and 52.
     * <p>This items are useful to add functionalities to the gui, so they aren't updated when the user changes page.
     *
     * @param inventory The inventory to modify.
     */
    protected void customizeInventory(@NotNull Inventory inventory) {
    }

}
