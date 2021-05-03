package com.fren_gor.invManagementPlugin;

import com.fren_gor.invManagementPlugin.command.BundleCommand;
import com.fren_gor.invManagementPlugin.command.ClaimItems;
import com.fren_gor.invManagementPlugin.command.FGive;
import com.fren_gor.invManagementPlugin.util.SavingUtil;
import com.fren_gor.invManagementPlugin.util.serializable.Bundle;
import lombok.Getter;
import com.fren_gor.invManagementPlugin.util.serializable.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InventoryManagementPlugin extends JavaPlugin implements Listener {

    @Getter
    private static InventoryManagementPlugin instance;

    private SavingUtil<Items> savings;

    private final Set<Player> items = new HashSet<>();

    @Getter
    private BundleManager bundleManager;

    @Getter
    private String unclaimedItems, actionbarUnclaimedItems, fgiveNoFreeSpace, claimitemsNoItemToClaim, claimitemsAllItemsClaimed;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        ConfigurationSerialization.registerClass(Items.class);
        ConfigurationSerialization.registerClass(Bundle.class);

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        saveResource("config.yml", false);

        try {
            getConfig().load(new File(getDataFolder(), "config.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        unclaimedItems = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.there-are-items-to-claim"));
        claimitemsNoItemToClaim = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.claimitems-no-items-to-claim"));
        claimitemsAllItemsClaimed = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.claimitems-all-items-has-been-claimed"));
        actionbarUnclaimedItems = ChatColor.translateAlternateColorCodes('&', getConfig().getString("actionbar.there-are-items-to-claim"));
        fgiveNoFreeSpace = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.fgive-not-enough-space"));

        savings = new SavingUtil<>(this, i -> i.getUuid().toString(), ".items");
        bundleManager = new BundleManager(this);
        new GuiListener(this);
        Bukkit.getPluginManager().registerEvents(this, this);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (savings.canLoad(p.getUniqueId().toString())) {
                items.add(p);
            }
        }

        PluginCommand claimitems = Bukkit.getPluginCommand("claimitems");
        if (claimitems != null) {
            ClaimItems cmd = new ClaimItems(this);
            claimitems.setExecutor(cmd);
            claimitems.setTabCompleter(cmd);
        }
        PluginCommand fgive = Bukkit.getPluginCommand("fgive");
        if (fgive != null) {
            FGive cmd = new FGive(this);
            fgive.setExecutor(cmd);
            fgive.setTabCompleter(cmd);
        }
        PluginCommand bundle = Bukkit.getPluginCommand("bundle");
        if (bundle != null) {
            BundleCommand cmd = new BundleCommand(bundleManager);
            bundle.setExecutor(cmd);
            bundle.setTabCompleter(cmd);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : items) {
                    sendUnclaimedMessage(p);
                }
            }
        }.runTaskTimer(this, 0, 1200);

    }

    public void sendUnclaimedMessage(Player p) {
        p.sendMessage("");
        p.sendMessage(unclaimedItems);
        p.sendMessage("");
    }

    @Nullable
    public Items loadItems(Player p) {
        if (!items.contains(p)) {
            return null;
        }
        try {
            return savings.load(p.getUniqueId().toString());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {

        if (savings.canLoad(e.getPlayer().getUniqueId().toString())) {
            items.add(e.getPlayer());
            sendUnclaimedMessage(e.getPlayer());
        }

    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        items.remove(e.getPlayer());
    }

    public boolean hasItems(Player p) {
        return items.contains(p);
    }

    public void remove(Player p) {
        if (items.remove(p)) {
            savings.remove(p.getUniqueId().toString());
        }
    }

    public void add(Player p, Items it) {
        if (items.add(p)) {
            savings.save(it);
        }
    }

    public void addAndSave(Player p, Items it) {
        items.add(p);
        savings.save(it);
    }

    public void save(Items i) {
        savings.save(i);
    }

}
