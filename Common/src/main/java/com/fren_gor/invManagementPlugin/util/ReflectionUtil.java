package com.fren_gor.invManagementPlugin.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

/**
 * Reflection class
 *
 * @author fren_gor
 */
@UtilityClass
public class ReflectionUtil {

    private static final String COMPLETE_VERSION = Bukkit.getServer().getClass().getName().split("\\.")[3];
    public static final int VERSION = Integer.parseInt(COMPLETE_VERSION.split("_")[1]);
    public static final int RELEASE = Integer.parseInt(COMPLETE_VERSION.split("R")[1]);

    /**
     * @param name The class name
     * @return The NMS class
     */
    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName(
                    "net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("[Reflection] Can't find NMS Class! (" + "net.minecraft.server."
                    + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
            return null;
        }
    }

    /**
     * @param name The class name
     * @return The CraftBukkit class
     */
    public static Class<?> getCBClass(String name) {
        try {
            return Class.forName(
                    "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("[Reflection] Can't find CB Class! (" + "org.bukkit.craftbukkit."
                    + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
            return null;
        }
    }

}
