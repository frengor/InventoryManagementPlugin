package com.fren_gor.invManagementPlugin.guis;

import com.fren_gor.invManagementPlugin.api.gui.BlockTopGuiInteractions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class LockedEmptyGui extends EmptyGui implements BlockTopGuiInteractions {

    public LockedEmptyGui(@NotNull Plugin plugin, @NotNull Player player, @NotNull String title, int size) {
        super(plugin, player, title, size);
    }
}
