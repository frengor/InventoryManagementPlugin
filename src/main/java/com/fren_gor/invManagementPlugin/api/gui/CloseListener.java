package com.fren_gor.invManagementPlugin.api.gui;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Implement alongside {@link org.bukkit.inventory.InventoryHolder} to listen to {@link InventoryCloseEvent}s.
 */
public interface CloseListener {

    void onClose(@NotNull InventoryCloseEvent event);

}
