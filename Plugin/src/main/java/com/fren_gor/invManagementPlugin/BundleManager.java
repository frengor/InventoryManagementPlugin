package com.fren_gor.invManagementPlugin;

import com.fren_gor.invManagementPlugin.util.serializable.Bundle;
import com.fren_gor.savingUtil.SavingUtil;
import com.fren_gor.savingUtil.SavingUtil.LoadResult;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        LoadResult<Bundle> b = savings.loadOrCorrupt(name);
        if (b.isCorrupted()) {
            return null;
        }
        return b.getObject();
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
