package com.fren_gor.invManagementPlugin.util;

import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

public class SavingUtil<S extends ConfigurationSerializable> {

    private final JavaPlugin instance;
    private final Function<S, String> getFileName;
    private File directory;

    public SavingUtil(JavaPlugin instance, Function<S, String> getFileName) {
        this.instance = Objects.requireNonNull(instance);
        this.getFileName = Objects.requireNonNull(getFileName);
        this.directory = instance.getDataFolder();
        if (!directory.exists())
            directory.mkdirs();
    }

    public SavingUtil(JavaPlugin instance, Function<S, String> getFileName, String subDirectory) {
        this.instance = Objects.requireNonNull(instance);
        this.getFileName = Objects.requireNonNull(getFileName);
        this.directory = new File(instance.getDataFolder(), Objects.requireNonNull(subDirectory));
        if (!directory.exists())
            directory.mkdirs();
    }

    public void save(@NotNull S s) {

        Validate.notNull(s);
        String name = getFileName.apply(s);
        File t = new File(directory, name + ".tmp");
        File f = new File(directory, name + ".dat");

        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("obj", s);

        try {
            yaml.save(t);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            Files.move(t, f);
        } catch (Exception e) {
            e.printStackTrace();
            Logger log = instance.getLogger();
            log.severe("Trying to save directly to " + f.getName());
            try {
                yaml.save(f);
            } catch (Exception e1) {
                log.severe("Couldn't save directly to " + f.getName());
                e1.printStackTrace();
                return;
            }
        }

        if (t.exists()) {
            t.delete();
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable S load(@NotNull String fileName) throws IOException, InvalidConfigurationException {
        Validate.notNull(fileName);

        File f = new File(directory, fileName + ".dat");

        if (!f.exists()) {
            return null;
        }

        YamlConfiguration yaml = new YamlConfiguration();

        yaml.load(f);

        return (S) yaml.get("obj");

    }

    public boolean canLoad(@NotNull String fileName) {
        return new File(directory, Objects.requireNonNull(fileName) + ".dat").exists();
    }

    public void remove(@NotNull S s) {
        Validate.notNull(s);
        File f = new File(directory, getFileName.apply(s) + ".dat");

        if (f.exists()) {
            f.delete();
        }
    }

    public boolean remove(@NotNull String fileName) {
        File f = new File(directory, Objects.requireNonNull(fileName) + ".dat");

        if (f.exists()) {
            f.delete();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public List<S> loadAll() {
        YamlConfiguration yaml = new YamlConfiguration();
        List<S> list = new LinkedList<>();
        Logger log = instance.getLogger();
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".dat")) {

                try {
                    yaml.load(f);
                } catch (Exception e) {
                    File f1 = new File(f.getPath() + ".corrupted");
                    log.severe("Couldn't load file " + f.getName() + ". Renaming it " + f1.getName());
                    e.printStackTrace();
                    try {
                        Files.move(f, f1);
                    } catch (Exception e1) {
                        log.severe("Couldn't rename " + f.getName() + " to " + f1.getName());
                        e1.printStackTrace();
                        continue;
                    }
                    f.delete();
                    continue;
                }

                S s = (S) yaml.get("obj");
                if (s == null) {
                    File f1 = new File(f.getPath() + ".corrupted");
                    log.severe("Couldn't load file " + f.getName() + ". Renaming it " + f1.getName());
                    try {
                        Files.move(f, f1);
                    } catch (Exception e1) {
                        log.severe("Couldn't rename " + f.getName() + " to " + f1.getName());
                        e1.printStackTrace();
                        continue;
                    }
                    f.delete();
                    continue;
                }
                list.add(s);
            }
        }
        return list;
    }

    public List<String> listExistingObjects() {
        List<String> list = new LinkedList<>();
        for (File f : directory.listFiles()) {
            String n = f.getName();
            if (n.endsWith(".dat")) {
                list.add(n.substring(0, n.length() - 4));
            }
        }
        return list;
    }

    public void copyFile(@NotNull String fileName, @NotNull String newName, @NotNull String folder) {

        Validate.notNull(fileName);
        Validate.notNull(newName);
        Validate.notNull(folder);

        File f = new File(directory, folder);
        if (!f.exists()) {
            f.mkdirs();
        }

        File old = new File(directory, fileName + ".dat");

        try {
            Files.move(old, new File(f, newName + ".corrupted"));
        } catch (Exception e) {
            instance.getLogger().severe("Couldn't rename " + old.getName() + " to " + newName + ".corrupted");
            e.printStackTrace();
        }

    }

}
