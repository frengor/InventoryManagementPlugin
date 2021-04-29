package com.fren_gor.invManagementPlugin.command;

import com.fren_gor.invManagementPlugin.gui.ClaimGui;
import lombok.RequiredArgsConstructor;
import com.fren_gor.invManagementPlugin.InventoryManagementPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ClaimItems implements CommandExecutor, TabCompleter {

    private final InventoryManagementPlugin instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player to perform this command.");
            return false;
        }

        Player p = (Player) sender;

        if (!instance.hasItems(p)) {
            p.sendMessage(instance.getClaimitemsNoItemToClaim());
            return false;
        }

        new ClaimGui(p);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
