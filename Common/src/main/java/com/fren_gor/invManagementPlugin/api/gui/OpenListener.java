package com.fren_gor.invManagementPlugin.api.gui;

import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Implement alongside {@link org.bukkit.inventory.InventoryHolder} to listen to {@link InventoryOpenEvent}s.
 */
public interface OpenListener {

    void onOpen(@NotNull InventoryOpenEvent event);

}
