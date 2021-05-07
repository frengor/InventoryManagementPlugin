package com.fren_gor.invManagementPlugin.command;

import com.fren_gor.invManagementPlugin.BundleManager;
import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import com.fren_gor.invManagementPlugin.guis.ConfirmGui;
import com.fren_gor.invManagementPlugin.guis.ConfirmGui.Result;
import com.fren_gor.invManagementPlugin.guis.EmptyGui;
import com.fren_gor.invManagementPlugin.util.serializable.Bundle;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BundleCommand implements CommandExecutor, TabCompleter {

    private static final List<String> OPTIONS = Arrays.asList("create", "list", "give", "delete");

    private final BundleManager manager;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /bundle <create|list|give|delete> ...");
            return false;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cYou must be a player to perform that subcommand.");
                return false;
            }
            if (args.length != 2) {
                sender.sendMessage("§cUsage: /bundle create <name>");
                return false;
            }
            if (manager.exists(args[1])) {
                sender.sendMessage("§cBundle " + args[1] + " already exists.");
                return false;
            }
            openEditor((Player) sender, args[1]);
        } else if (args[0].equalsIgnoreCase("list")) {
            List<String> l = manager.list();
            if (l.size() == 0) {
                sender.sendMessage("§eNo bundle is registered.");
                return true;
            }
            StringJoiner j = new StringJoiner("§8, §7");
            for (String s : l) {
                j.add(s);
            }
            sender.sendMessage("§eBundles: §7" + j);
        } else if (args[0].equalsIgnoreCase("give")) {
            if (args.length != 3) {
                sender.sendMessage("§cUsage: /bundle give <player> <bundle>");
                return false;
            }
            Player p = Bukkit.getPlayer(args[1]);
            if (p == null) {
                sender.sendMessage("§cPlayer " + args[1] + " doesn't exists.");
                return false;
            }
            Bundle b = manager.loadBundle(args[2]);
            if (b == null) {
                sender.sendMessage("§cBundle " + args[2] + " doesn't exists.");
                return false;
            }
            SafeInventoryActions.partialAdd(p, b.getItems().toArray(new ItemStack[0]));
            sender.sendMessage("§aSuccessfully gave bundle to " + args[1] + '.');
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length != 2) {
                sender.sendMessage("§cUsage: /bundle delete <bundle>");
                return false;
            }
            if (manager.remove(args[1])) {
                // Bundle got deleted
                sender.sendMessage("§aSuccessfully deleted bundle " + args[1] + '.');
            } else {
                // Bundle doesn't exists
                sender.sendMessage("§cBundle " + args[1] + " doesn't exists.");
                return false;
            }
        } else {
            sender.sendMessage("§cUsage: /bundle <create|list|give|delete> ...");
            return false;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1) {
            return filterTabCompleteOptions(OPTIONS, args);
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(args[1])).collect(Collectors.toList());
            } else if (args.length == 3) {
                return filterTabCompleteOptions(manager.list(), args);
            }
        } else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
            return filterTabCompleteOptions(manager.list(), args);
        }
        return Collections.emptyList();
    }

    private void openEditor(@NotNull Player p, @NotNull String name) {
        new EmptyGui(manager.getPlugin(), p, "Creating bundle: " + name, 54) {
            @Override
            public void onClose(@NotNull InventoryCloseEvent e) {
                ItemStack[] it = e.getInventory().getStorageContents();
                List<ItemStack> l = new ArrayList<>(it.length);
                for (ItemStack i : it) {
                    if (i != null && i.getType() != Material.AIR)
                        l.add(i.clone());
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new ConfirmGui(plugin, player, "Create bundle?", r -> {
                            if (r == Result.YES) {
                                manager.save(new Bundle(name, l));
                                player.sendMessage("§aSuccessfully created bundle " + name + '.');
                            } else {
                                SafeInventoryActions.addItems(player.getInventory(), l);
                                player.sendMessage("§cNo changes has been applied.");
                            }
                        }).openInventory();
                    }
                }.runTask(manager.getPlugin());
            }
        }.openInventory();
    }

    private static List<String> filterTabCompleteOptions(Collection<String> options, String... args) {
        String lastArg = args.length > 0 ? args[(args.length - 1)].toLowerCase() : "";
        return options.stream().filter(s -> s.startsWith(lastArg)).collect(Collectors.toList());
    }

}
