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
import java.util.UUID;

public class Items implements ConfigurationSerializable, Cloneable {

    @Getter
    private final UUID uuid;
    @Getter
    private final List<ItemStack> items;

    public Items(@NotNull UUID uuid) {
        this.uuid = uuid;
        this.items = new LinkedList<>();
    }

    public Items(@NotNull UUID uuid, @NotNull List<ItemStack> items) {
        this.uuid = uuid;
        this.items = items;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("items", ItemStackSerializer.serializeItemStack(items));
        return map;
    }

    public static Items deserialize(Map<String, Object> args) {
        try {
            return new Items(UUID.fromString((String) args.get("uuid")),
                    ItemStackSerializer.deserializeItemStack((List<String>) args.get("items")));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object clone() {
        return new Items(uuid, items);
    }

}
