package com.fren_gor.invManagementPlugin.api;

import com.fren_gor.invManagementPlugin.util.HeadUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Base64;

public enum Heads {

    ARROW_UP("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDQ4Yjc2OGM2MjM0MzJkZmIyNTlmYjNjMzk3OGU5OGRlYzExMWY3OWRiZDZjZDg4ZjIxMTU1Mzc0YjcwYjNjIn19fQ=="),
    ARROW_DOWN("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmRhZGQ3NTVkMDg1MzczNTJiZjdhOTNlM2JiN2RkNGQ3MzMxMjFkMzlmMmZiNjcwNzNjZDQ3MWY1NjExOTRkZCJ9fX0="),
    ARROW_LEFT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ViZjkwNzQ5NGE5MzVlOTU1YmZjYWRhYjgxYmVhZmI5MGZiOWJlNDljNzAyNmJhOTdkNzk4ZDVmMWEyMyJ9fX0="),
    ARROW_RIGHT("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2ZjFhMjViNmJjMTk5OTQ2NDcyYWVkYjM3MDUyMjU4NGZmNmY0ZTgzMjIxZTU5NDZiZDJlNDFiNWNhMTNiIn19fQ==");

    private final ItemStack item;

    Heads(String texture) {
        this.item = getHead(texture);
    }

    /**
     * Get the head.
     *
     * @return The {@link ItemStack} of this head.
     */
    public ItemStack getItem() {
        return item.clone();
    }

    /**
     * Get a head based on an encoded skin texture.<br>
     * An encoded texture looks like:<br>
     * "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3..."
     *
     * @param texture The encoded skin texture.
     * @return The head with skin applied.
     */
    public static ItemStack getHead(String texture) {
        return HeadUtil.getHead(texture);
    }

    /**
     * Get a head based on a url.<br>
     * An url looks like:<br>
     * "http://textures.minecraft.net/texture/d48b768c623432dfb259fb3c3978..."
     *
     * @param url The skin url.
     * @return The head with skin applied.
     */
    public static ItemStack getHeadFromURL(String url) {
        return getHead(Base64.getEncoder().encodeToString(String.format("{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}", url).getBytes()));
    }

    /**
     * Get a head based on a player.
     *
     * @param player The player.
     * @return The head of the player.
     */
    public static ItemStack getHeadFromPlayer(Player player) {
        return HeadUtil.getPlayerHead(player);
    }

}
