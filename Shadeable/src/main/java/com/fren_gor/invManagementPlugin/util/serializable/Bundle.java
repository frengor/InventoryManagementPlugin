package com.fren_gor.invManagementPlugin.util.serializable;

import com.fren_gor.invManagementPlugin.util.ItemStackSerializer;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Bundle implements ConfigurationSerializable, Cloneable {

    @Getter
    private final String name;
    @Getter
    private final List<ItemStack> items;

    public Bundle(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
        this.items = new LinkedList<>();
    }

    public Bundle(@NotNull String name, @NotNull List<ItemStack> items) {
        this.name = Objects.requireNonNull(name);
        this.items = Objects.requireNonNull(items);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("items", ItemStackSerializer.serializeItemStack(items));
        return map;
    }

    public static Bundle deserialize(Map<String, Object> args) {
        try {
            return new Bundle((String) args.get("name"), ItemStackSerializer.deserializeItemStack((List<String>) args.get("items")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object clone() {
        return new Bundle(name, items);
    }

}
