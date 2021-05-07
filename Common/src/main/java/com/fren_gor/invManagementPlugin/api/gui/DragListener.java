package com.fren_gor.invManagementPlugin.api.gui;

import org.bukkit.event.inventory.InventoryDragEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Implement alongside {@link org.bukkit.inventory.InventoryHolder} to listen to {@link InventoryDragEvent}s.
 */
public interface DragListener {

    void onDrag(@NotNull InventoryDragEvent event);

}
