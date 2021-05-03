package com.fren_gor.invManagementPlugin.api.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Implement alongside {@link org.bukkit.inventory.InventoryHolder} to listen to {@link InventoryClickEvent}s.
 */
public interface ClickListener {

    void onClick(@NotNull InventoryClickEvent event);

}
