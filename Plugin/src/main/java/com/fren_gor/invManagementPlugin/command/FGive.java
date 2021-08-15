package com.fren_gor.invManagementPlugin.command;

import com.fren_gor.invManagementPlugin.api.SafeInventoryActions;
import com.fren_gor.invManagementPlugin.util.ReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class FGive implements CommandExecutor, TabCompleter {

    // fgive <target> <item> [count]
    private static Field handle;
    private static Method asCraftCopy, setTag, parse;

    static {
        final Class<?> craftItemStack = ReflectionUtil.getCBClass("inventory.CraftItemStack");
        final Class<?> nmsItemStack = ReflectionUtil.getNMSClass("ItemStack", "world.item");
        final Class<?> nmsNBTTagCompound = ReflectionUtil.getNMSClass("NBTTagCompound", "nbt");
        final Class<?> nmsMojangsonParser = ReflectionUtil.getNMSClass("MojangsonParser", "nbt");

        try {
            asCraftCopy = craftItemStack.getDeclaredMethod("asCraftCopy", ItemStack.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            handle = craftItemStack.getDeclaredField("handle");
            handle.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        try {
            setTag = nmsItemStack.getDeclaredMethod("setTag", nmsNBTTagCompound);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            parse = nmsMojangsonParser.getDeclaredMethod("parse", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /" + label + " <player> <item> [amount]");
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);

        if (p == null) {
            sender.sendMessage("§cPlayer " + args[0] + " isn't online.");
            return false;
        }

        int amount = 1;

        boolean hasAmount = args.length > 2 && !args[args.length - 1].endsWith("}");

        if (hasAmount) {
            String a = args[args.length - 1];
            try {
                amount = Integer.parseInt(a);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount \"" + a + '\"');
                return false;
            }
            if (amount <= 0) {
                sender.sendMessage("§cInvalid amount \"" + a + '\"');
                return false;
            }
        }

        // item{}

        StringJoiner j = new StringJoiner(" ");

        int max = hasAmount ? args.length - 1 : args.length;

        for (int i = 1; i < max; i++) {
            j.add(args[i]);
        }

        String arg = j.toString();
        int index = arg.indexOf('{');

        Material m;
        try {
            m = Material.valueOf(index == -1 ? arg.toUpperCase() : arg.substring(0, index).toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid item \"" + arg + '\"');
            return false;
        }

        ItemStack item = new ItemStack(m, amount);

        if (index != -1) {

            try {
                item = (ItemStack) asCraftCopy.invoke(null, item);
                Object nbt = parse.invoke(null, arg.substring(index));
                Object it = handle.get(item);
                setTag.invoke(it, nbt);
            } catch (Exception e) {
                if (e.getClass().getSimpleName().equals("CommandSyntaxException"))
                    sender.sendMessage("§cInvalid item NBT \"" + arg.substring(index) + '\"');
                else {
                    e.printStackTrace();
                    sender.sendMessage("§cInternal error: couldn't paste NBTs to item.");
                }
                return false;
            }

        }

        SafeInventoryActions.partialAdd(p, item);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length <= 1) {
            String lastArg = args.length > 0 ? args[(args.length - 1)].toLowerCase() : "";
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.startsWith(lastArg)).collect(Collectors.toList());
        }
        if (args.length == 2 && !args[1].contains("{")) {
            return Arrays.stream(Material.values()).map(m -> m.toString().toLowerCase()).filter(m -> !m.contains("legacy") && m.startsWith(args[1])).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
