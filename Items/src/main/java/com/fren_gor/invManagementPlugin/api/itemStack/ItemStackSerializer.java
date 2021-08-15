package com.fren_gor.invManagementPlugin.api.itemStack;

import com.fren_gor.invManagementPlugin.exceptions.DeserializationException;
import com.fren_gor.invManagementPlugin.util.ReflectionUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.fren_gor.invManagementPlugin.util.ReflectionUtil.RELEASE;
import static com.fren_gor.invManagementPlugin.util.ReflectionUtil.VERSION;

/**
 * From: <a href="https://www.spigotmc.org/threads/how-to-serialize-itemstack-inventory-with-attributestorage.152931/#post-3077661">https://www.spigotmc.org/threads/how-to-serialize-itemstack-inventory-with-attributestorage.152931/#post-3077661</a><br>
 * Optimized by fren_gor
 *
 * @author MIOR, fren_gor
 */
@UtilityClass
public final class ItemStackSerializer {

    private static Constructor<?> nbtTagCompoundConstructor, nmsItemStackConstructor;
    private static Method aIn, aOut, createStack, asBukkitCopy, asNMSCopy, save, getTitle;
    private static final byte INV_VERSION = 0x01;

    static {
        Class<?> nbtTagCompoundClass = ReflectionUtil.getNMSClass("NBTTagCompound", "nbt");
        Class<?> nmsItemStackClass = ReflectionUtil.getNMSClass("ItemStack", "world.item");
        Class<?> nbtCompressedStreamToolsClass = ReflectionUtil.getNMSClass("NBTCompressedStreamTools", "nbt");
        Class<?> craftItemStackClass = ReflectionUtil.getCBClass("inventory.CraftItemStack");
        try {
            nbtTagCompoundConstructor = nbtTagCompoundClass.getDeclaredConstructor();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            aIn = nbtCompressedStreamToolsClass.getMethod("a", VERSION < 16 || (VERSION == 16 && RELEASE == 1) ? DataInputStream.class : DataInput.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            aOut = nbtCompressedStreamToolsClass.getMethod("a", nbtTagCompoundClass, DataOutput.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            if (VERSION < 11) {
                createStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass);
            } else {
                nmsItemStackConstructor = nmsItemStackClass.getDeclaredConstructor(nbtTagCompoundClass);
                nmsItemStackConstructor.setAccessible(true);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            asBukkitCopy = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            asNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        try {
            save = nmsItemStackClass.getMethod("save", nbtTagCompoundClass);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
        if (VERSION < 14) {
            try {
                getTitle = Inventory.class.getDeclaredMethod("getTitle");
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    public static ItemStack deserializeItemStack(String data) {
        if (data == null || data.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            Object nbtTagCompound = aIn.invoke(null, dataInputStream);
            Object craftItemStack = craftNMSItemStack(nbtTagCompound);
            return (ItemStack) asBukkitCopy.invoke(null, craftItemStack);
        } catch (ReflectiveOperationException | IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static ItemStack[] deserializeItemStack(String[] data) {
        Validate.notNull(data, "Data cannot be null");
        ItemStack[] arr = new ItemStack[data.length];
        for (int i = 0; i < data.length; i++) {
            arr[i] = deserializeItemStack(data[i]);
        }
        return arr;
    }

    public static List<ItemStack> deserializeItemStack(List<String> data) {
        Validate.notNull(data, "Data cannot be null");
        List<ItemStack> l = new ArrayList<>(data.size());
        for (String s : data) {
            l.add(deserializeItemStack(s));
        }
        return l;
    }

    public static String serializeItemStack(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return "";
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream)) {
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = asNMSCopy.invoke(null, item);
            save.invoke(nmsItemStack, nbtTagCompound);
            aOut.invoke(null, nbtTagCompound, dataOutput);
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (ReflectiveOperationException | IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String[] serializeItemStack(ItemStack[] items) {
        Validate.notNull(items, "Items cannot be null");
        String[] arr = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            arr[i] = serializeItemStack(items[i]);
        }
        return arr;
    }

    public static List<String> serializeItemStack(List<ItemStack> items) {
        Validate.notNull(items, "Items cannot be null");
        List<String> l = new ArrayList<>(items.size());
        for (ItemStack s : items) {
            l.add(serializeItemStack(s));
        }
        return l;
    }

    public static String serializeInventory(Inventory inv) {
        Validate.notNull(inv, "Inventory cannot be null");
        Validate.isTrue(inv.getType() == InventoryType.CHEST,
                "Illegal inventory type " + inv.getType() + "(expected CHEST).");

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutput = new DataOutputStream(outputStream)) {
            dataOutput.writeByte(INV_VERSION);

            dataOutput.writeByte(inv.getSize());
            if (VERSION < 14) {
                dataOutput.writeBoolean(true);
                dataOutput.writeUTF((String) getTitle.invoke(inv));
            } else {
                dataOutput.writeBoolean(false);
            }

            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack it = inv.getItem(i);
                if (it == null || it.getType() == Material.AIR)
                    continue;
                dataOutput.writeByte(i);
                dataOutput.writeUTF(serializeItemStack(it));
            }

            dataOutput.writeByte(-1);

            /*
             * Version - 1 byte
             * Size - 1 byte
             * NextIsPresent - Boolean
             * Title - String (Present only if the previous is true)
             * Array:
             *   SlotIndex - 1 byte
             *   ItemStack - String
             * -1 (Array End) - 1 byte
             */
            return new BigInteger(1, outputStream.toByteArray()).toString(32);
        } catch (ReflectiveOperationException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static InventoryMap deserializeInventory(String data) {
        Validate.notNull(data, "Data cannot be null");
        Validate.isTrue(!data.isEmpty(), "Data cannot be empty");

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new BigInteger(data, 32).toByteArray());
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {

            int version = dataInputStream.readByte();

            if (version != INV_VERSION)
                throw new DeserializationException("Invalid inventory version \"" + version
                        + "\". The only supported version is the  \"" + INV_VERSION + "\".");

            int size = dataInputStream.readByte();
            boolean present = dataInputStream.readBoolean();
            String title = present ? dataInputStream.readUTF() : null;

            InventoryMap map = new InventoryMap(size, size, title);

            while (true) {
                int slot = dataInputStream.readByte();
                if (slot == -1 || slot >= size)
                    break;
                ItemStack it = deserializeItemStack(dataInputStream.readUTF());

                map.put(slot, it);
            }

            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an object (designed for {@link YamlConfiguration#get(String)}) and try to get an {@link ItemStack} out of it.
     * <p>
     * The object must be an ItemStack or the result of {@link ItemStackSerializer#serializeItemStack(ItemStack)}.
     *
     * @param obj Object to be transformed to an {@link ItemStack}
     * @return An {@link ItemStack} if the deserialization was successful, otherwise null.
     * @throws {@link DeserializationException} If the object is not an ItemStack or the result of {@link ItemStackSerializer#serializeItemStack(ItemStack)}.
     */
    public static ItemStack deserializeObject(Object obj) throws DeserializationException {
        if (obj instanceof ItemStack) {
            return (ItemStack) obj;
        } else if (obj instanceof String) {
            return ItemStackSerializer.deserializeItemStack((String) obj);
        } else
            throw new DeserializationException("Couldn't deserialize object");
    }

    private static Object craftNMSItemStack(Object nbtTagCompound) throws ReflectiveOperationException {
        if (VERSION < 11) {
            return createStack.invoke(null, nbtTagCompound);
        } else {
            return nmsItemStackConstructor.newInstance(nbtTagCompound);
        }
    }
}
