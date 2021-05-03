package com.fren_gor.invManagementPlugin.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

/**
 * Reflection class
 *
 * @author fren_gor
 */
@UtilityClass
public class ReflectionUtil {

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

    /**
     * Get the complete server version
     *
     * @return The server version
     */
    public static String getCompleteVersion() {
        return Bukkit.getServer().getClass().getName().split("\\.")[3];
    }

    @Getter
    private static final int version = Integer.parseInt(getCompleteVersion().split("_")[1]);
    @Getter
    private static final int release = Integer.parseInt(getCompleteVersion().split("R")[1]);

}
