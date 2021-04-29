package com.fren_gor.invManagementPlugin.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@UtilityClass
public class HeadUtil {

    private static Method setProfile, getProfile/*, getHandle*/;

    static {
        try {
            setProfile = ReflectionUtil.getCBClass("inventory.CraftMetaSkull").getDeclaredMethod("setProfile", GameProfile.class);
            setProfile.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        final Class<?> craftPlayer = ReflectionUtil.getCBClass("entity.CraftPlayer");
        try {
            //getHandle = craftPlayer.getDeclaredMethod("getHandle");
            getProfile = craftPlayer.getDeclaredMethod("getProfile");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public static ItemStack getHead(@NotNull String texture) {
        Validate.notNull(texture);
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            setProfile.invoke(headMeta, profile);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return playerHead;
        }
        playerHead.setItemMeta(headMeta);
        return playerHead;
    }

    public static ItemStack getPlayerHead(@NotNull Player player) {
        Validate.notNull(player);
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        GameProfile gp;
        try {
            gp = (GameProfile) getProfile.invoke(player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            headMeta.setOwningPlayer(player);
            playerHead.setItemMeta(headMeta);
            return playerHead;
        }
        try {
            setProfile.invoke(headMeta, gp);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            headMeta.setOwningPlayer(player);
            playerHead.setItemMeta(headMeta);
            return playerHead;
        }
        playerHead.setItemMeta(headMeta);
        return playerHead;
    }
}
