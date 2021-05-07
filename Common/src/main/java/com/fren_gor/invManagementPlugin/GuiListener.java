package com.fren_gor.invManagementPlugin;

import com.fren_gor.invManagementPlugin.api.gui.BlockGuiDragging;
import com.fren_gor.invManagementPlugin.api.gui.BlockGuiInteractions;
import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiDragging;
import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiInteractions;
import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import com.fren_gor.invManagementPlugin.api.gui.DragListener;
import com.fren_gor.invManagementPlugin.api.gui.OpenListener;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

final class GuiListener implements Listener {

    public GuiListener(@NotNull Plugin plugin) {
        Validate.notNull(plugin, "Plugin is null.");
        Validate.isTrue(plugin.isEnabled(), "Plugin is not enabled.");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDrag(InventoryDragEvent e) {
        InventoryHolder topHolder = e.getView().getTopInventory().getHolder();
        InventoryHolder holder = e.getInventory().getHolder();
        if (topHolder instanceof BlockGuiInteractions || topHolder instanceof BlockGuiDragging) {
            e.setCancelled(true);
        } else if (holder instanceof BlockTopGuiInteractions || holder instanceof BlockTopGuiDragging) {
            int size = e.getInventory().getSize();
            for (int i : e.getRawSlots()) {
                if (i < size) {
                    e.setCancelled(true);
                    break;
                }
            }
        }

        if (holder instanceof DragListener) {
            ((DragListener) holder).onDrag(e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onItemMoveInventory(InventoryMoveItemEvent e) {
        if ((e.getSource().getHolder() instanceof BlockGuiInteractions || e.getDestination().getHolder() instanceof BlockGuiInteractions) || (e.getSource().getHolder() instanceof BlockTopGuiInteractions || e.getDestination().getHolder() instanceof BlockTopGuiInteractions)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onClick(InventoryClickEvent e) {

        // Not merging ifs for clearness
        if (e.getView().getTopInventory().getHolder() instanceof BlockGuiInteractions || (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof BlockTopGuiInteractions)) {
            e.setCancelled(true);
        } else if (e.getView().getTopInventory().getHolder() instanceof BlockTopGuiInteractions) {
            if (/*e.getAction() == InventoryAction.HOTBAR_SWAP || */e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SHIFT_LEFT ||
                    e.getClick() == ClickType.SHIFT_RIGHT) {
                e.setCancelled(true);
            }
        }

        if (e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof ClickListener) {
            ((ClickListener) e.getClickedInventory().getHolder()).onClick(e);
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof OpenListener) {
            ((OpenListener) e.getInventory().getHolder()).onOpen(e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CloseListener) {
            ((CloseListener) e.getInventory().getHolder()).onClose(e);
        }
    }

    public void unregister() {
        InventoryDragEvent.getHandlerList().unregister(this);
        InventoryMoveItemEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        InventoryCloseEvent.getHandlerList().unregister(this);
        InventoryOpenEvent.getHandlerList().unregister(this);
    }

}
