package com.fren_gor.invManagementPlugin.guis;

import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiInteractions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LockedGui extends EditableGui implements BlockTopGuiInteractions {

    public LockedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size) {
        super(plugin, player, title, size);
    }

    public LockedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size, ItemStack... initialItems) {
        super(plugin, player, title, size, initialItems);
    }

    public LockedGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size, @NotNull List<@Nullable ItemStack> initialItems) {
        super(plugin, player, title, size, initialItems);
    }

}
