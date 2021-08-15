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
    public static final boolean IS_1_17 = VERSION >= 17;

    /**
     * @param name The class name
     * @return The NMS class
     */
    public static Class<?> getNMSClass(String name, String mcPackaage) {
        String path = "net.minecraft." + (IS_1_17 ? mcPackaage : "server." + COMPLETE_VERSION) + '.' + name;
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("[Reflection] Can't find NMS Class! (" + path + ")");
            return null;
        }
    }

    /**
     * @param name The class name
     * @return The CraftBukkit class
     */
    public static Class<?> getCBClass(String name) {
        try {
            return Class.forName("org.bukkit.craftbukkit." + COMPLETE_VERSION + "." + name);
        } catch (ClassNotFoundException e) {
            Bukkit.getLogger().info("[Reflection] Can't find CB Class! (" + "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
            return null;
        }
    }

}
