package com.fren_gor.invManagementPlugin.guis;

import com.fren_gor.invManagementPlugin.api.gui.BlockGuiInteractions;
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

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A simple confirmation gui.
 */
public final class ConfirmGui implements InventoryHolder, BlockTopGuiInteractions, ClickListener, CloseListener {

    /**
     * The result of a {@link ConfirmGui}
     */
    public enum Result {
        /**
         * The player said YES.
         */
        YES,
        /**
         * The player said NO.
         */
        NO,
        /**
         * The player closed the gui.
         */
        CANCEL;
    }

    private static final ItemStack CONFIRM, REJECT;

    static {
        CONFIRM = new ItemStack(Material.GREEN_WOOL);
        ItemMeta m = CONFIRM.getItemMeta();
        m.setDisplayName("§aYes");
        CONFIRM.setItemMeta(m);
        REJECT = new ItemStack(Material.RED_WOOL);
        m = REJECT.getItemMeta();
        m.setDisplayName("§cNo");
        REJECT.setItemMeta(m);
    }

    @Getter
    private final Player player;
    @Getter
    private final String title;
    private final Consumer<Result> resultConsumer;
    private boolean alreadyExecuted = false;

    /**
     * Creates a new ConfirmGui and <strong>opens it to the player</strong>.
     *
     * @param plugin The plugin creating this gui
     * @param player The player to open the gui to
     * @param title The gui title
     * @param resultConsumer The consumer to execute after the player has made a choice.
     */
    public ConfirmGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, @NotNull Consumer<Result> resultConsumer) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        this.player = Objects.requireNonNull(player);
        this.title = Objects.requireNonNull(title);
        this.resultConsumer = Objects.requireNonNull(resultConsumer);
        Bukkit.getScheduler().callSyncMethod(plugin, () -> player.openInventory(getInventory()));
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(this, 45, title);
        inv.setItem(21, CONFIRM.clone());
        inv.setItem(23, REJECT.clone());
        return inv;
    }

    @Override
    public void onClick(@NotNull InventoryClickEvent e) {
        switch (e.getSlot()) {
            case 21: // Yes
                resultConsumer.accept(Result.YES);
                alreadyExecuted = true;
                player.closeInventory();
                break;
            case 23: // No
                resultConsumer.accept(Result.NO);
                alreadyExecuted = true;
                player.closeInventory();
                break;
        }
    }

    @Override
    public void onClose(@NotNull InventoryCloseEvent e) {
        if (!alreadyExecuted)
            resultConsumer.accept(Result.CANCEL);
    }

}
