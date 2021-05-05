package com.fren_gor.invManagementPlugin;

import com.fren_gor.invManagementPlugin.util.serializable.Bundle;
import com.fren_gor.savingUtil.SavingUtil;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public final class BundleManager {

    @Getter
    private final InventoryManagementPlugin plugin;
    @Getter
    private final SavingUtil<Bundle> savings;

    public BundleManager(InventoryManagementPlugin plugin) {
        this.plugin = plugin;
        savings = new SavingUtil<>(plugin, Bundle::getName, ".bundles");
    }

    @Nullable
    public Bundle loadBundle(@NotNull String name) {
        try {
            return savings.load(name);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> list() {
        return savings.listExistingObjects();
    }

    public boolean exists(String name) {
        return savings.canLoad(name);
    }

    public boolean remove(String name) {
        return savings.remove(name);
    }

    public void save(Bundle bundle) {
        savings.save(bundle);
    }

}
