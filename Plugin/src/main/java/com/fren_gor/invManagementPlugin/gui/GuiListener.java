package com.fren_gor.invManagementPlugin.gui;

import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import com.fren_gor.invManagementPlugin.api.gui.BlockGuiInteractions;
import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiInteractions;
import com.fren_gor.invManagementPlugin.api.gui.ClickListener;
import com.fren_gor.invManagementPlugin.api.gui.CloseListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class GuiListener implements Listener {

    public GuiListener(InventoryManagementPlugin instance) {
        Bukkit.getPluginManager().registerEvents(this, instance);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof BlockGuiInteractions) {
            e.setCancelled(true);
        } else if (e.getInventory().getHolder() instanceof BlockTopGuiInteractions) {
            int size = e.getInventory().getSize();
            for (int i : e.getRawSlots()) {
                if (i < size) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemMoveInventory(InventoryMoveItemEvent e) {
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

    @EventHandler
    private void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CloseListener) {
            ((CloseListener) e.getInventory().getHolder()).onClose(e);
        }
    }

}
